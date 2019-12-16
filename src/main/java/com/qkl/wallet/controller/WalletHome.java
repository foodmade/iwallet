package com.qkl.wallet.controller;

import com.qkl.wallet.common.Const;
import com.qkl.wallet.contract.Usdt;
import com.qkl.wallet.service.WalletService;
import com.qkl.wallet.vo.ResultBean;
import com.qkl.wallet.vo.in.BalanceParams;
import com.qkl.wallet.vo.in.WithdrawParams;
import com.qkl.wallet.vo.out.BalanceResponse;
import com.qkl.wallet.vo.out.CreateWalletResponse;
import com.qkl.wallet.vo.out.GasResponse;
import com.qkl.wallet.vo.out.WithdrawResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.DefaultGasProvider;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigDecimal;
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
    public ResultBean<CreateWalletResponse> createWallet(){
        return ResultBean.success(walletService.createWallet());
    }

    /**
     * 提币
     */
    @PostMapping(value = "withdraw")
    public ResultBean<WithdrawResponse> withdraw(@RequestBody WithdrawParams params) throws IOException {
        return ResultBean.success(walletService.withdraw(params));
    }

    /**
     * 0xfe1dd454d3e6f947dae40cf9a773247eac44883a
     * 获取代币余额
     * @return 代币余额
     */
    @GetMapping(value = "getTokenBalance")
    public ResultBean<BalanceResponse> getTokenBalance(@RequestParam @NotNull(message = "Address must not be null") String address,
                                                       @RequestParam @NotNull(message = "TokenType must not be null") String tokenType){
        return ResultBean.success(walletService.getTokenBalance(address,tokenType));
    }

    /**
     * 获取ETH钱包余额
     * @param address 钱包地址
     */
    @GetMapping(value = "/getETHBalance")
    public ResultBean<BalanceResponse> getETHBalance(@RequestParam @NotNull(message = "Address must not be null") String address){
        return ResultBean.success(walletService.getETHBalance(address));
    }

    /**
     * 获取平台余额
     */
    @PostMapping(value = "/getPlatformBalance")
    public ResultBean<BalanceResponse> getPlatformBalance(@RequestBody @Valid  BalanceParams balanceParams){
        return ResultBean.success(walletService.getPlatformBalance(balanceParams));
    }

    /**
     * 以太币之间的转账
     */
    @GetMapping(value = "/eth_transfer")
    public ResultBean<Boolean> eth_transfer(@RequestParam @NotNull(message = "Address must not be null")String toAddress,
                                            @RequestParam @NotNull(message = "The transaction amount cannot be empty")BigDecimal amount){
        return ResultBean.success(walletService.transferEth(toAddress,amount));
    }

    @PostMapping(value = "/get_eth_gas")
    public ResultBean<GasResponse> getEthGas(){
        return ResultBean.success(walletService.getEthGas());
    }

    /**
     * 部署合约代码
     */
    @GetMapping(value = "deployContract")
    public ResultBean deployContract(@RequestParam String address,@RequestParam String type) throws Exception {
        Credentials credentials = Credentials.create(address);
//        Token myToken = Token.deploy(web3j,credentials,new DefaultGasProvider(),BigInteger.valueOf(2500).multiply(Const._UNIT),type,type).send();
        Usdt myToken = Usdt.deploy(web3j,credentials,new DefaultGasProvider(),BigInteger.valueOf(2500).multiply(Const._TOKEN_UNIT),type,type,BigInteger.valueOf(100)).send();
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
