package com.qkl.wallet.service.impl;

import com.alibaba.fastjson.JSON;
import com.qkl.wallet.common.Const;
import com.qkl.wallet.common.exception.InvalidException;
import com.qkl.wallet.common.walletUtil.LightWallet;
import com.qkl.wallet.common.walletUtil.outModel.WalletAddressInfo;
import com.qkl.wallet.config.ApplicationConfig;
import com.qkl.wallet.contract.Token;
import com.qkl.wallet.service.TransactionManageService;
import com.qkl.wallet.service.WalletService;
import com.qkl.wallet.vo.ResultBean;
import com.qkl.wallet.vo.in.WithdrawRequest;
import com.qkl.wallet.vo.out.BalanceResponse;
import com.qkl.wallet.vo.out.CreateWalletResponse;
import com.qkl.wallet.vo.out.WithdrawResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @Author xiaom
 * @Date 2019/11/21 14:59
 * @Version 1.0.0
 * @Description <>
 **/
@Service
@Slf4j
public class WalletServiceImpl implements WalletService {

    @Autowired
    private Web3j web3j;
    @Autowired
    private TransactionManageService transactionManageService;
    @Autowired
    private EventService eventService;

    @Override
    public CreateWalletResponse createWallet() {
        try {
            WalletAddressInfo walletAddressInfo = LightWallet.createNewWallet(ApplicationConfig.defaultPassword);
            Credentials credentials = LightWallet.openWallet(ApplicationConfig.defaultPassword, walletAddressInfo.getName());
            String privateKey = Numeric.toHexStringNoPrefix(credentials.getEcKeyPair().getPrivateKey());
            return new CreateWalletResponse(credentials.getAddress(),walletAddressInfo.getName(),privateKey);
        } catch (Exception e) {
            log.error("Create wallet throw error . throw info >>> [{}]",e.getMessage());
            return null;
        }
    }

    @Override
    public WithdrawResponse withdraw(List<WithdrawRequest> withdrawRequests) throws IOException {
        //Invalid withdraw order list.
        withdrawRequests = validOrderRequest(withdrawRequests);

        if (withdrawRequests.isEmpty()) {
            return new WithdrawResponse("");
        }
        //Load contract client.
        Token myToken = LightWallet.loadTokenClient(web3j);

        log.info("Contract valid:[{}] address:[{}]", myToken.isValid(), myToken.getContractAddress());

        if (!myToken.isValid()) {
            throw new InvalidException("Contract address invalid. Please contact the administrator to redeploy");
        }

        for (WithdrawRequest withdrawRequest : withdrawRequests) {
            //Cache this order basis info.
            transactionManageService.cacheTransactionOrder(withdrawRequest);

            log.info("Start submitting a transfer request.");
            CompletableFuture<TransactionReceipt> future = myToken
                    .transfer(withdrawRequest.getAddress(), withdrawRequest.getAmount().toBigInteger().multiply(Const._UNIT))
                    .sendAsync();
            log.info("Transaction request submitted. Start listening thread.");

            new Thread(() -> {
                try {
                    TransactionReceipt receipt = future.get();
                    log.info("The transaction has been confirmed. >>>> :[{}]", JSON.toJSONString(receipt));
                    eventService.addSuccessEvent(receipt, withdrawRequest);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }).start();

            log.info("Transfer successful. >>> Waiting for blockchain confirmation transaction.");
        }
        return new WithdrawResponse("");
    }

    private List<WithdrawRequest> validOrderRequest(List<WithdrawRequest> withdrawRequests) {

        List<WithdrawRequest> validList = new ArrayList<>();

        for (WithdrawRequest withdrawRequest : withdrawRequests) {
            try {
                Assert.notNull(withdrawRequest.getAddress(),"Transfer to address must not be null.");
                Assert.notNull(withdrawRequest.getAmount(),"Transfer amount must not be null.");
                Assert.isTrue(withdrawRequest.getAmount().compareTo(BigDecimal.ZERO) > 0,"Negative or zero amount are not allowed");
                validList.add(withdrawRequest);
            } catch (Exception e) {
                log.error(e.getMessage());
                log.error("Throw withdraw order info:[{}]",JSON.toJSONString(withdrawRequest));
                eventService.addErrEvent(withdrawRequest,e.getMessage());
            }
        }
        return validList;

    }

    @Override
    public ResultBean<BalanceResponse> getTokenBalance(String address) {
        try {
            Assert.notNull(address,"Wallet address must not be null.");
            //Load contract client.
            return ResultBean.success(new BalanceResponse(getTokenBalanceOfAddress(address)));
        }catch (Exception e){
            log.error("Query contract address balance throw error. >>> [{}]",e.getMessage());
            return ResultBean.exception(e.getMessage());
        }
    }

    @Override
    public ResultBean<BalanceResponse> getETHBalance(String address) {
        return null;
    }

    private BigInteger getTokenBalanceOfAddress(String address) throws ExecutionException, InterruptedException {
        Token myToken = LightWallet.loadTokenClient(web3j);
        return myToken.balanceOf(address).sendAsync().get();
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
    }

}
