package com.qkl.wallet.core.event.listener;

import com.alibaba.fastjson.JSON;
import com.qkl.wallet.common.Const;
import com.qkl.wallet.common.SpringContext;
import com.qkl.wallet.common.walletUtil.LightWallet;
import com.qkl.wallet.common.walletUtil.WalletUtils;
import com.qkl.wallet.core.event.TransferEvent;
import com.qkl.wallet.core.transfer.OrderManage;
import com.qkl.wallet.core.transfer.OrderModel;
import com.qkl.wallet.core.transfer.work.WorkThread;
import com.qkl.wallet.domain.RawTransactionResEntity;
import lombok.extern.slf4j.Slf4j;
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
public class TransferEventListener {


    @EventListener
    public void onApplicationEvent(TransferEvent event) {
        OrderModel orderModel =  event.getOrder();
        log.info("TransferEventListener process message. TokenName:[{}] trace:[{}]",orderModel.getTokenName(),orderModel.getWithdraw().getTrace());
        try {

            String contractAddress = orderModel.getContractAddress();
            String toAddress = orderModel.getWithdraw().getAddress();
            BigDecimal amount = orderModel.getWithdraw().getAmount().multiply(new BigDecimal(Const._UNIT));
            String trace = orderModel.getWithdraw().getTrace();
            String tokenName = orderModel.getTokenName();
            String fromAddress = orderModel.getFromAddress();

            //同步交易转账
//            WalletUtils.syncTransferToken(tokenName,toAddress,amount.toBigInteger(),trace);

            //离线交易转账
            RawTransactionResEntity entity = WalletUtils.offlineTransferToken(contractAddress,toAddress,amount.toBigInteger(),fromAddress);
            //将当前txHash缓存到redis,用于在充值回调的校验,区分是提现订单还是充值订单
            OrderManage.addWithdrawTxHashNumber(entity.getTransactionHash(),trace);
            //由于区块链的转账规则,每次交易的nonce随机数必须是递增并且不一致的随机数,所以,这里需要等待区块链处理完毕并且更新nonce才能继续处理下一笔订单
            WalletUtils.monitorNonceIsUpdate(entity.getNonce(),fromAddress);
            //unlock work thread.
            ((WorkThread)event.getSource()).play();
        } catch (Exception e) {
            log.error("TransferEventListener throw error. message:[{}]",e.getMessage());
            e.printStackTrace();
        }
    }


}
