package com.qkl.wallet;

import com.alibaba.fastjson.JSON;
import com.qkl.wallet.config.ApplicationConfig;
import com.qkl.wallet.contract.MyToken;
import com.qkl.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

@SpringBootTest
class WalletApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void testCreateWallet() throws ExecutionException, InterruptedException, IOException, CipherException {

        //设置需要的矿工费
        BigInteger GAS_PRICE = BigInteger.valueOf(22_000_000_000L);
        BigInteger GAS_LIMIT = BigInteger.valueOf(4_300_000);

        //调用的是kovan测试环境，这里使用的是infura这个客户端
        Web3j web3j = Web3j.build(new HttpService("https://kovan.infura.io/v3/ef40dd3c018349959f1509fb679ea67d"));

        //转账人账户地址
        String ownAddress = "0x493fb23d930458a84b49b5ca53d961e039868a58";
        //被转人账户地址
        String toAddress = "0x6e27727bbb9f0140024a62822f013385f4194999";
        //转账人私钥
        Credentials credentials = Credentials.create("A41686728F41287B31DCC360D251BFC38C1B2BCB95EDC735D4D96A87D2FF4A55");

//        Credentials credentials = WalletUtils.loadCredentials(
//                       "chen19960119",
//        "src/main/resources/UTC--2018-03-01T05-53-37.043Z--d1c82c71cc567d63fd53d5b91dcac6156e5b96b3");


        //getNonce（这里的Nonce我也不是很明白，大概是交易的笔数吧）
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(ownAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();

        //创建交易，这里是转0.5个以太币
        BigInteger value = Convert.toWei("0.5", Convert.Unit.ETHER).toBigInteger();
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
             nonce, GAS_PRICE, GAS_LIMIT, toAddress, value);

        //签名Transaction，这里要对交易做签名
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        //发送交易
        EthSendTransaction ethSendTransaction =
              web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        String transactionHash = ethSendTransaction.getTransactionHash();
        //获得到transactionHash后就可以到以太坊的网站上查询这笔交易的状态了
        System.out.println(transactionHash);

    }

    @Test
    public void testTrsans() throws Exception {

        //调用的是kovan测试环境，这里使用的是infura这个客户端
        Web3j web3j = Web3j.build(new HttpService("https://kovan.infura.io/v3/ef40dd3c018349959f1509fb679ea67d"));

        //转账人账户地址
        String ownAddress = "0x493fb23d930458a84b49b5ca53d961e039868a58";
        //被转人账户地址
        String toAddress = "0x6e27727bbb9f0140024a62822f013385f4194999";
        //转账人私钥
        Credentials credentials = Credentials.create("A41686728F41287B31DCC360D251BFC38C1B2BCB95EDC735D4D96A87D2FF4A55");

        TransactionReceipt transactionReceipt = Transfer.sendFunds(
                web3j, credentials, toAddress,
                BigDecimal.valueOf(0.2), Convert.Unit.ETHER).send();

        System.out.println(transactionReceipt.getTransactionHash());
    }

    @Test
    public void testContract() throws Exception {
        //调用的是kovan测试环境，这里使用的是infura这个客户端
        Web3j web3j = Web3j.build(new HttpService(ApplicationConfig.blockHost));

        //转账人账户地址
        String ownAddress = "0x493fb23d930458a84b49b5ca53d961e039868a58";
        //被转人账户地址
        String toAddress = "0x6e27727bbb9f0140024a62822f013385f4194999";

        //转账人私钥
        Credentials credentials = Credentials.create(ApplicationConfig.secretKey);

//        MyToken myToken = MyToken.deploy(web3j,credentials,new DefaultGasProvider(),BigInteger.valueOf(100),BigInteger.valueOf(100000),"BBT",BigInteger.valueOf(10),"T").send();
        MyToken myToken = MyToken.load(ApplicationConfig.contractAddress,web3j,credentials, Contract.GAS_PRICE,Contract.GAS_LIMIT);
        System.out.println("合约部署完毕 状态:" + myToken.isValid() + " 地址：" + myToken.getContractAddress());


        System.out.println("主账户余额：" + toDecimal(2,myToken.getMasterBalance().send()));
    }

    @Autowired
    private WalletService walletService;

    @Test
    public void testCreateWallet1(){
        JSON.toJSONString(walletService.createWallet());
    }

    public static String toDecimal(int decimal,BigInteger integer){
//		String substring = str.substring(str.length() - decimal);
        StringBuffer sbf = new StringBuffer("1");
        for (int i = 0; i < decimal; i++) {
            sbf.append("0");
        }
        String balance = new BigDecimal(integer).divide(new BigDecimal(sbf.toString()), 18, BigDecimal.ROUND_DOWN).toPlainString();
        return balance;
    }

}
