package com.qkl.wallet.controller;

import com.alibaba.fastjson.JSON;
import com.qkl.wallet.config.ApplicationConfig;
import com.qkl.wallet.contract.MyToken;
import com.qkl.wallet.vo.in.WithdrawRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.qkl.wallet.common.UtilsService.toDecimal;

/**
 * @Author xiaom
 * @Date 2019/11/20 16:32
 * @Version 1.0.0
 * @Description <钱包接口控制中心>
 **/
@RestController
@RequestMapping(value = "/wallet")
@Validated
public class WalletHome {

    @Autowired
    private Web3j web3j;

    /**
     * 提现
     */
    @PostMapping(value = "withdraw")
    public HashMap withdraw(@RequestBody WithdrawRequest withdrawRequest){
        return null;
    }

    @GetMapping(value = "test")
    public HashMap test() throws Exception{

        String account = "0x810f2EFe6E06820E1ee9357A2a61Df1a09466482";

        Credentials credentials = Credentials.create(ApplicationConfig.secretKey);
        MyToken myToken = MyToken.load(ApplicationConfig.contractAddress,web3j,credentials, Contract.GAS_PRICE,Contract.GAS_LIMIT);
        System.out.println("合约部署完毕 状态:" + myToken.isValid() + " 地址：" + myToken.getContractAddress());

        CompletableFuture<TransactionReceipt> future = myToken.transfer(account,BigInteger.valueOf(1000)).sendAsync();

        new Thread(() -> {
            try {
                TransactionReceipt receipt = future.get();
                System.out.println("转账完成 JSON:" + JSON.toJSONString(receipt));

                System.out.println("主账户余额：" + toDecimal(2,myToken.getMasterBalance().send()));
                System.out.println("次账户余额：" + toDecimal(2,myToken.getBalance(account).send()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();



        System.out.println("主账户余额：" + toDecimal(2,myToken.getMasterBalance().send()));
        System.out.println("次账户余额：" + toDecimal(2,myToken.getBalance(account).send()));

        return new HashMap();
    }

    @GetMapping(value = "createWallet")
    public HashMap createWallet(){

        return null;
    }

}
