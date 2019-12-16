package com.qkl.wallet.service.impl;

import com.alibaba.fastjson.JSON;
import com.qkl.wallet.common.Const;
import com.qkl.wallet.common.enumeration.CallbackTypeEnum;
import com.qkl.wallet.common.enumeration.ExceptionEnum;
import com.qkl.wallet.common.exception.BadRequestException;
import com.qkl.wallet.common.walletUtil.WalletUtils;
import com.qkl.wallet.core.transfer.OrderManage;
import com.qkl.wallet.domain.ConfirmListenerEntity;
import com.qkl.wallet.domain.EthTransactionReq;
import com.qkl.wallet.domain.TransactionListenerEvent;
import com.qkl.wallet.service.HubService;
import com.qkl.wallet.vo.out.WithdrawCallback;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @Author Jackies
 * @Date 2019/12/14 19:39
 * @Description TODO://
 **/
@Service
@Slf4j
public class HubServiceImpl implements HubService {

    @Autowired
    private EventService eventService;

    @Override
    public Boolean submitTransferEvent(TransactionListenerEvent event) {

        log.info("Received information from the order monitor.....");
        log.info("Detail info:{}", JSON.toJSONString(event));
        //判断是充值订单的回调还是提现订单的回调
        CallbackTypeEnum callbackTypeEnum = judgeOrderType(event.getTransactionHash());
        log.info("Current order is [{}]",callbackTypeEnum.getDesc());

        //打包提交回业务服务器
        WithdrawCallback callback = new WithdrawCallback(callbackTypeEnum);
        callback.setTxnHash(event.getTransactionHash());
        callback.setAmount(WalletUtils.unitCover(new BigDecimal(event.getReturnValues().getValue())) + "");
        callback.setSender(event.getReturnValues().getFrom());
        callback.setRecepient(event.getReturnValues().getTo());
        callback.set_id(event.getId());
        callback.setTrace(OrderManage.getTraceId(event.getTransactionHash()));
        callback.setTokenName(event.getTokenName());
        eventService.addSuccessEvent(callback);
        return true;
    }

    @Override
    public Boolean confirmBlockNumEvent(ConfirmListenerEntity confirmListenerEntity) {
        log.info("Number of confirmation blocks received from the order listener");
        log.info("Detail info:{}",JSON.toJSONString(confirmListenerEntity));

        WithdrawCallback callback = new WithdrawCallback(CallbackTypeEnum.CONFIRM_TYPE);
        callback.setTxnHash(confirmListenerEntity.getTransactionHash());
        callback.setConfirmBlockNumber(confirmListenerEntity.getConfirmNumber());
        eventService.addSuccessEvent(callback);
        return true;
    }

    @Override
    public Boolean ethSubmitTransferEvent(EthTransactionReq ethTransactionReq) {

        //由于ETH的交易是通过block监听,这是属于共有块,所以有可能监控到的不是此服务器发起的交易,这儿只接受从此钱包服务创建的钱包地址交易
        if(!WalletUtils.validWalletAddress(ethTransactionReq.getTo())){
            log.info("This transaction address:[{}] does not belong to the current wallet service",ethTransactionReq.getTo());
            return false;
        }

        log.info("ETH Received information from the order monitor.....");
        log.info("ETH Detail info:{}", JSON.toJSONString(ethTransactionReq));
        //判断是充值订单的回调还是提现订单的回调
        CallbackTypeEnum callbackTypeEnum = judgeOrderType(ethTransactionReq.getHash());
        log.info("ETH Current order is [{}]",callbackTypeEnum.getDesc());

        WithdrawCallback callback = new WithdrawCallback(callbackTypeEnum);
        callback.setTxnHash(ethTransactionReq.getHash());
        callback.setSender(ethTransactionReq.getFrom());
        callback.setGas(ethTransactionReq.getGas());
        callback.setRecepient(ethTransactionReq.getTo());
        callback.setAmount(WalletUtils.unitCover(ethTransactionReq.getValue()) + "");
        callback.setTrace(OrderManage.getTraceId(ethTransactionReq.getHash()));
        callback.setTokenName(ethTransactionReq.getTokenName());
        eventService.addSuccessEvent(callback);
        return true;
    }

    /**
     * 检查当前交易属于充值还是提现
     */
    private CallbackTypeEnum judgeOrderType(String transactionHash) {
        if(StringUtils.isBlank(transactionHash)){
            throw new BadRequestException(ExceptionEnum.PARAMS_MISS_ERR);
        }

        if(OrderManage.isWithdrawExist(transactionHash)){
            return CallbackTypeEnum.WITHDRAW_TYPE;
        }else{
            return CallbackTypeEnum.RECHARGE_TYPE;
        }
    }
}
