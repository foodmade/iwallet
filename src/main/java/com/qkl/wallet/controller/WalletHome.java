package com.qkl.wallet.controller;

import com.qkl.wallet.contract.Token;
import com.qkl.wallet.service.WalletService;
import com.qkl.wallet.vo.ResultBean;
import com.qkl.wallet.vo.in.WithdrawParams;
import com.qkl.wallet.vo.out.BalanceResponse;
import com.qkl.wallet.vo.out.CreateWalletResponse;
import com.qkl.wallet.vo.out.WithdrawResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;

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
    public CreateWalletResponse createWallet(){
        return walletService.createWallet();
    }

    /**
     * 提币
     */
    @PostMapping(value = "withdraw")
    public WithdrawResponse withdraw(@RequestBody WithdrawParams params) throws IOException {
        return walletService.withdraw(params.getRequest());
    }

    /**
     * 0xfe1dd454d3e6f947dae40cf9a773247eac44883a
     * 获取代币余额
     * @return
     */
    @GetMapping(value = "getTokenBalance")
    public ResultBean<BalanceResponse> getBalance(@RequestParam String address){
        return walletService.getTokenBalance(address);
    }

    /**
     * 部署合约代码
     * @return
     */
    @GetMapping(value = "deployContract")
    public ResultBean deployContract(@RequestParam String address,@RequestParam String type) throws Exception {
        Web3j web3j = Web3j.build(new HttpService());
        Credentials credentials = Credentials.create(address);
        Token myToken = Token.deploy(web3j,credentials,new DefaultGasProvider(),BigInteger.valueOf(10000000).multiply(BigInteger.valueOf(1000000000000000000L)),type,type).send();
        System.out.println("合约部署完毕 状态:" + myToken.isValid() + " 地址：" + myToken.getContractAddress());
        return ResultBean.success(null);
    }

    @GetMapping(value = "test")
    public HashMap test() throws Exception{

//        String account = "0x810f2EFe6E06820E1ee9357A2a61Df1a09466482";
        String form = "0x493fb23d930458a84b49b5ca53d961e039868a58";
        String to = "0x50E13802e0c9f84AF4c48CAc39acaF83be28397A";


        Credentials credentials = Credentials.create("EA9047A5DFD2197545D683E4E8DE61ECC02F08D215DA7CA7F6433C06FAB140FA");
//        MyToken myToken = MyToken.load(ApplicationConfig.contractAddress,web3j,credentials, Contract.GAS_PRICE,Contract.GAS_LIMIT);
//        MyToken myToken = MyToken.load("0x9ace0861dd9fe9d87007aca6b3059dffba4dd0d2",web3j,credentials, Contract.GAS_PRICE,Contract.GAS_LIMIT);
//        System.out.println("合约部署完毕 状态:" + myToken.isValid() + " 地址：" + myToken.getContractAddress());
//
//        Boolean future = myToken.transferFrom(form,to,BigInteger.valueOf(1000)).sendAsync().get();

//        new Thread(() -> {
//            try {
//                Boolean receipt = future.get();
//                System.out.println("转账完成 JSON:" + JSON.toJSONString(receipt));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();

//        System.out.println("boolean:"+future);

        return new HashMap();
    }
}
