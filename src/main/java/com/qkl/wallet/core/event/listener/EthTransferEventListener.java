package com.qkl.wallet.core.event.listener;

import com.alibaba.fastjson.JSON;
import com.qkl.wallet.common.Const;
import com.qkl.wallet.common.tools.IOCUtils;
import com.qkl.wallet.common.walletUtil.LightWallet;
import com.qkl.wallet.common.walletUtil.WalletUtils;
import com.qkl.wallet.core.event.TransferEvent;
import com.qkl.wallet.core.manage.OrderManage;
import com.qkl.wallet.domain.OrderModel;
import com.qkl.wallet.core.transfer.work.OrderWorkThread;
import com.qkl.wallet.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @Author Jackies
 * @Date 2019/12/15 18:06
 * @Description TODO://
 **/
@Component
@Slf4j
public class EthTransferEventListener extends Listener{

    @Autowired
    private WalletService walletService;

    @EventListener
    public void onApplicationEvent(TransferEvent event) {
        OrderModel orderModel = event.getOrder();
        try {
            WalletService walletService = IOCUtils.getWalletService();
            log.info("TransferEventListener process message. TokenName:[{}] to:[{}] trace:[{}]",
                    orderModel.getTokenName(), orderModel.getWithdraw().getAddress(), orderModel.getWithdraw().getTrace());

            String toAddress = orderModel.getWithdraw().getAddress();
            String fromAddress = orderModel.getWithdraw().getFromAddress();

            BigDecimal amount = orderModel.getWithdraw().getAmount();

            String secretKey = walletService.foundTokenSecretKey(fromAddress);
            String trace = orderModel.getWithdraw().getTrace();

            BigInteger gas = walletService.getTotalGasPrice();


            //Valid system wallet account balance is it enough.
            BigDecimal systemWalletBalance = walletService.getETHBalance(fromAddress).getBalance();

            //Check whether the balance is sufficient
            Assert.isTrue((gas.compareTo(systemWalletBalance.toBigInteger()) > 0),"insufficient gas");

            log.info("ETH transferEth function process........................");

            BigDecimal gasPrice = WalletUtils.unitEthCover(gas);

            if(new BigDecimal(-1).compareTo(amount) < 0){
                log.info("platform wallet transfer to people wallet. Platform balance:[{}] transfer amount:[{}]",systemWalletBalance,amount);
                Assert.isTrue(amount.compareTo(systemWalletBalance) < 0,"Insufficient available balance in system account");
            }else{
                log.info("People wallet transfer to platform wallet. People wallet balance:[{}] gas:[{}]",systemWalletBalance,gasPrice);
                //针对划入划出的特殊处理,如果是划出操作,则将用户所有钱包余额转到平台钱包
                amount = systemWalletBalance.subtract(gasPrice);
            }

            BigInteger nonce = WalletUtils.getNonce(fromAddress);

            Assert.notNull(secretKey, "ETH transfer kill. Because the platform wallet secretKey is empty");
            TransactionReceipt receipt = walletService.transferEth(fromAddress, toAddress, amount, secretKey);

            log.info("Eth transfer successful. Receipt transactionHash:[{}]", receipt.getTransactionHash());
            OrderManage.addWithdrawTxHashNumber(receipt.getTransactionHash(), JSON.toJSONString(loadBaseOrder(trace, orderModel.getTxnType())));
            //nonce随机数检测
            WalletUtils.monitorNonceIsUpdate(nonce, fromAddress);
        } catch (Exception e) {
            log.error("TransferEventListener ETH throw error. message:[{}]", e.getMessage());
            callbackErrMessage(orderModel, e.getMessage());
            e.printStackTrace();
        } finally {
            ((OrderWorkThread) event.getSource()).play();
        }
    }

}
