package com.qkl.wallet.controller;

import com.alibaba.fastjson.JSON;
import com.qkl.wallet.common.RedisUtil;
import com.qkl.wallet.config.ApplicationConfig;
import com.qkl.wallet.contract.MyToken;
import com.qkl.wallet.service.WalletService;
import com.qkl.wallet.vo.ResultBean;
import com.qkl.wallet.vo.in.WithdrawRequest;
import com.qkl.wallet.vo.out.BalanceResponse;
import com.qkl.wallet.vo.out.CreateWalletResponse;
import com.qkl.wallet.vo.out.WithdrawResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
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

    @Autowired
    private WalletService walletService;
    /**
     * 创建钱包
     */
    @PostMapping(value = "getWallet")
    public ResultBean<CreateWalletResponse> createWallet(){
        return walletService.createWallet();
    }

    /**
     * 提币
     */
    @PostMapping(value = "withdraw")
    public ResultBean<WithdrawResponse> withdraw(@RequestBody WithdrawRequest withdrawRequest){
        return walletService.withdraw(withdrawRequest);
    }

    /**
     * 0xfe1dd454d3e6f947dae40cf9a773247eac44883a
     * 获取代币余额
     * @return
     */
    @GetMapping(value = "getBalance")
    public ResultBean<BalanceResponse> getBalance(@RequestParam String address){
        return walletService.getBalance(address);
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
}
