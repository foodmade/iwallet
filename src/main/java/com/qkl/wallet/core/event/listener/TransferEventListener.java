package com.qkl.wallet.core.event.listener;

import com.alibaba.fastjson.JSON;
import com.qkl.wallet.common.tools.IOCUtils;
import com.qkl.wallet.common.walletUtil.LightWallet;
import com.qkl.wallet.common.walletUtil.WalletUtils;
import com.qkl.wallet.core.event.TokenTransferEvent;
import com.qkl.wallet.core.event.TransferEvent;
import com.qkl.wallet.core.transfer.OrderManage;
import com.qkl.wallet.core.transfer.OrderModel;
import com.qkl.wallet.core.transfer.work.WorkThread;
import com.qkl.wallet.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigDecimal;

/**
 * @Author Jackies
 * @Date 2019/12/15 18:06
 * @Description TODO://
 **/
@Component
@Slf4j
public class TransferEventListener {

    @EventListener
    public void onApplicationEvent(TransferEvent event) {
        try {
            OrderModel orderModel = event.getOrder();
            WalletService walletService = IOCUtils.getWalletService();
            log.info("TransferEventListener process message. TokenName:[{}] to:[{}] trace:[{}]",
                    orderModel.getTokenName(),orderModel.getWithdraw().getAddress(),orderModel.getWithdraw().getTrace());

            String toAddress = orderModel.getWithdraw().getAddress();
            String fromAddress = orderModel.getFromAddress();
            BigDecimal amount = orderModel.getWithdraw().getAmount();
            String secretKey = walletService.foundTokenSecretKey(orderModel.getTokenName());
            String trace = orderModel.getWithdraw().getTrace();
            Assert.notNull(secretKey,"ETH transfer kill. Because the platform wallet secretKey is empty");
            TransactionReceipt receipt = walletService.transferEth(fromAddress,toAddress,amount,secretKey);

            log.info("Eth transfer successful. Receipt transactionHash:[{}]",receipt.getTransactionHash());
            OrderManage.addWithdrawTxHashNumber(receipt.getTransactionHash(),trace);
            //nonce随机数检测
            WalletUtils.monitorNonceIsUpdate(LightWallet.getNonce(fromAddress),fromAddress);
        }catch (Exception e){
            log.error("TransferEventListener ETH throw error. message:[{}]",e.getMessage());
            e.printStackTrace();
        }finally {
            ((WorkThread)event.getSource()).play();
        }
    }


}