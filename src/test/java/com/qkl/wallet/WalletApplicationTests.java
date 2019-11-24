package com.qkl.wallet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qkl.wallet.common.HttpUtils;
import com.qkl.wallet.config.ApplicationConfig;
import com.qkl.wallet.contract.MyToken;
import com.qkl.wallet.contract.Token;
import com.qkl.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
import java.util.HashMap;
import java.util.Map;
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
        Web3j web3j = Web3j.build(new HttpService());
//        Web3j web3j = Web3j.build(new HttpService("https://kovan.infura.io/v3/ef40dd3c018349959f1509fb679ea67d"));

        //转账人账户地址
//        String ownAddress = "0x493fb23d930458a84b49b5ca53d961e039868a58";
        String ownAddressLocal = "0x7e30710300837D0F174Eaa896638BDFfaaA2C2f0";
        //被转人账户地址
        String toAddress = "0x6e27727bbb9f0140024a62822f013385f4194999";

        String contractAddressLoalhost = "0xcc561e689ece612751ac7dfcf252f66518b1c274";

        //4A8B70A5DC05C82972AC79D9733010E6FE1D394D7C634ED703D2A7F7072D45CE  account3

        //转账人私钥
//        Credentials credentials = Credentials.create(ApplicationConfig.secretKey);
//        Credentials credentials = Credentials.create("A41686728F41287B31DCC360D251BFC38C1B2BCB95EDC735D4D96A87D2FF4A55");
        Credentials credentials = Credentials.create("ED8C110972FBFF97574A53D5029893514BD9333E97F369A749CD46033275C6D7");

//        MyToken myToken = MyToken.deploy(web3j,credentials,new DefaultGasProvider(),BigInteger.valueOf(10000000),BigInteger.valueOf(1000000000000000000L),"CNM",BigInteger.valueOf(18),"CNM").send();
//        Token myToken = Token.deploy(web3j,credentials,new DefaultGasProvider(),BigInteger.valueOf(10000000).multiply(BigInteger.valueOf(1000000000000000000L)),"CNM","CNM").send();

//        Token myToken = Token.load("0x009b3D84760caa9ee6792c58184476166F4D1221",web3j,credentials, Contract.GAS_PRICE,Contract.GAS_LIMIT);
        Token myToken = Token.load(contractAddressLoalhost,web3j,credentials, Contract.GAS_PRICE,Contract.GAS_LIMIT);
        System.out.println("合约部署完毕 状态:" + myToken.isValid() + " 地址：" + myToken.getContractAddress());

        TransactionReceipt receipt = myToken.transfer(toAddress,BigInteger.valueOf(2000L).multiply(BigInteger.valueOf(1000000000000000000L))).sendAsync().get();
        System.out.println("完毕："+JSON.toJSONString(receipt));
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

    @Test
    public void testHtp(){
        String url = "http://127.0.0.1:9008/admin/financeManage/callbackWallet";
        Map<String,String> map = new HashMap<>();
        map.put("_id","1111111");
        map.put("sender","0xSend");
        ResponseEntity<JSONObject> responseEntity =  HttpUtils.postForEntity(url,map);
        System.out.println(JSON.toJSONString(responseEntity.getBody()));
    }
}
