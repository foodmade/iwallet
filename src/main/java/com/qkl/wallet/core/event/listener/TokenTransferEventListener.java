package com.qkl.wallet.core.event.listener;
import com.alibaba.fastjson.JSON;
import com.qkl.wallet.common.Const;
import com.qkl.wallet.common.enumeration.CallbackTypeEnum;
import com.qkl.wallet.common.enumeration.ChainEnum;
import com.qkl.wallet.common.tools.IOCUtils;
import com.qkl.wallet.common.walletUtil.WalletUtils;
import com.qkl.wallet.core.event.TokenTransferEvent;
import com.qkl.wallet.core.manage.OrderManage;
import com.qkl.wallet.domain.OrderModel;
import com.qkl.wallet.core.transfer.work.OrderWorkThread;
import com.qkl.wallet.domain.RawTransactionResEntity;
import com.qkl.wallet.service.WalletService;
import com.qkl.wallet.service.impl.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @Author Jackies
 * @Date 2019/12/14 14:30
 * @Description TODO://
 **/
@Component
@Slf4j
public class TokenTransferEventListener extends Listener{

    @Autowired
    private WalletService walletService;
    @Autowired
    private EventService eventService;

    @EventListener
    public void onApplicationEvent(TokenTransferEvent event) {
        OrderModel orderModel =  event.getOrder();
        try {
            String contractAddress = orderModel.getContractAddress();
            String toAddress = orderModel.getWithdraw().getAddress();
            BigDecimal amount = orderModel.getWithdraw().getAmount().multiply(new BigDecimal(orderModel.getDecimals()));
            String trace = orderModel.getWithdraw().getTrace();
            String fromAddress = orderModel.getFromAddress();

            if(toAddress == null && fromAddress == null){
                throw new Exception("Token transfer execute failed. Because toAddress and FromAddress all empty");
            }

            String platformAddress = IOCUtils.getWalletService().foundPlatformAddress(orderModel.getTokenName(), ChainEnum.ETH.getChainName());

            //这里是为了兼容划出功能,如果存在一个地址为空,则默认使用平台钱包地址
            if(fromAddress == null){
                fromAddress = platformAddress;
            }else if(toAddress == null){
                toAddress = platformAddress;
            }

            String secretKey = walletService.foundTokenSecretKey(fromAddress);

            log.info("TokenTransferEventListener process message. TokenName:[{}] amount:[{}] toAddress:[{}] fromAddress:[{}] trace:[{}] secretKey:[{}]",
                    orderModel.getTokenName(),amount,toAddress,fromAddress,trace,secretKey);

            //离线交易转账
            RawTransactionResEntity entity = WalletUtils.offlineTransferToken(contractAddress,toAddress,amount.toBigInteger(),fromAddress,secretKey);

            //将当前txHash缓存到redis,用于在充值回调的校验,区分是提现订单还是充值订单
            OrderManage.addWithdrawTxHashNumber(entity.getTransactionHash(), JSON.toJSONString(loadBaseOrder(trace,orderModel.getTxnType())));
            //由于区块链的转账规则,每次交易的nonce随机数必须是递增并且不一致的随机数,所以,这里需要等待区块链处理完毕并且更新nonce才能继续处理下一笔订单
            WalletUtils.monitorNonceIsUpdate(entity.getNonce(),fromAddress);
        } catch (Exception e) {
            log.error("TokenTransferEventListener submit throw error. message:[{}]",e.getMessage());
            callbackErrMessage(orderModel,e.getMessage());
            e.printStackTrace();
        }finally {
            //unlock work thread.
            ((OrderWorkThread)event.getSource()).play();
        }
    }
}
