package com.qkl.wallet.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qkl.wallet.common.exception.InvalidException;
import com.qkl.wallet.common.walletUtil.LightWallet;
import com.qkl.wallet.common.walletUtil.outModel.WalletAddressInfo;
import com.qkl.wallet.config.ApplicationConfig;
import com.qkl.wallet.contract.MyToken;
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
import org.web3j.tx.Contract;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
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

    @Override
    public ResultBean<CreateWalletResponse> createWallet() {
        try {
            WalletAddressInfo walletAddressInfo = LightWallet.createNewWallet(ApplicationConfig.defaultPassword);
            Credentials credentials = LightWallet.openWallet(ApplicationConfig.defaultPassword, walletAddressInfo.getName());
            String privateKey = Numeric.toHexStringNoPrefix(credentials.getEcKeyPair().getPrivateKey());
            return ResultBean.success(new CreateWalletResponse(credentials.getAddress(),walletAddressInfo.getName(),privateKey));
        } catch (Exception e) {
            log.error("Create wallet throw error . throw info >>> [{}]",e.getMessage());
            return ResultBean.exception(e.getMessage());
        }
    }

    @Override
    public ResultBean<WithdrawResponse> withdraw(WithdrawRequest withdrawRequest) {
        try {

            Assert.notNull(withdrawRequest.getAddress(),"Transfer to address must not be null.");
            Assert.notNull(withdrawRequest.getAmount(),"Transfer amount must not be null.");
            Assert.isTrue(withdrawRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0,"Negative or zero amount are not allowed");
            //Load contract client.
            MyToken myToken = LightWallet.loadTokenClient(web3j);
            log.info("Contract valid:[{}] address:[{}]",myToken.isValid(),myToken.getContractAddress());

            if(!myToken.isValid()){
                throw new InvalidException("Contract address invalid. Please contact the administrator to redeploy");
            }
            log.info("Start submitting a transfer request.");
            TransactionReceipt transactionReceipt = myToken.transfer(withdrawRequest.getAddress(),withdrawRequest.getAmount().toBigInteger()).sendAsync().get();
            log.info("Transfer successful. >>> txHash Json:{}", JSON.toJSONString(transactionReceipt));
            return ResultBean.success(new WithdrawResponse(withdrawRequest.getAddress(),withdrawRequest.getAmount()));
        }catch (InvalidException ex){
            log.error(ex.getMessage());
            return ResultBean.exception(ex.getMessage());
        } catch (Exception e){
            log.error("Wallet service internal throw error. exMsg:[{}]",e.getMessage());
            return ResultBean.exception(e.getMessage());
        }
    }

    @Override
    public ResultBean<BalanceResponse> getBalance(String address) {
        try {
            Assert.notNull(address,"Wallet address must not be null.");
            //Load contract client.
            return ResultBean.success(new BalanceResponse(getBalanceOfAddress(address)));
        }catch (Exception e){
            log.error("Query contract address balance throw error. >>> [{}]",e.getMessage());
            return ResultBean.exception(e.getMessage());
        }
    }

    private BigInteger getBalanceOfAddress(String address) throws ExecutionException, InterruptedException {
        MyToken myToken = LightWallet.loadTokenClient(web3j);
        return myToken.getBalance(address).sendAsync().get();
    }
}
