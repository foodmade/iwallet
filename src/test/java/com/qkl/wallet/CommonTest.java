package com.qkl.wallet;

import com.alibaba.fastjson.JSON;
import com.qkl.wallet.common.Const;
import com.qkl.wallet.common.cache.JedisKey;
import com.qkl.wallet.common.SpringContext;
import com.qkl.wallet.common.tools.IOCUtils;
import com.qkl.wallet.common.walletUtil.LightWallet;
import com.qkl.wallet.common.walletUtil.WalletUtils;
import com.qkl.wallet.vo.out.CreateWalletResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeDecoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.tx.Contract;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

/**
 * @Author Jackies
 * @Date 2019/12/15 15:46
 * @Description TODO://
 **/
public class CommonTest extends WalletApplicationTests {

    @Autowired
    private Web3j web3j;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void testBlockNumber(){
        CreateWalletResponse response = new CreateWalletResponse("0xdF67ab61A941f4001a95255c57f586e7f99421f9","TEST_WALLET","86030747098382DD1F94C513E6B4AF9EADA76B75ADEF08CA96864DD14382A18D");
        WalletUtils.saveWalletInfo(response);

    }

    @Test
    public void testTokenSignTransfer() throws Exception {

        String toAddress = "0x493fb23d930458a84b49B5cA53D961e039868A58";
        String fromAddress = "0x7e30710300837D0F174Eaa896638BDFfaaA2C2f0";
        String contractAddress = "0x19ba60a0d3eae900761078c536b1b065937671a8";
        String key = "ED8C110972FBFF97574A53D5029893514BD9333E97F369A749CD46033275C6D7";
        BigInteger amount = new BigInteger("101").multiply(Const._TOKEN_UNIT);


        BigInteger GAS_PRICE = Contract.GAS_PRICE;
        BigInteger GAS_LIMIT = Contract.GAS_LIMIT;

        Function function = new Function(
                "transfer",
                Arrays.asList(new Address(toAddress), new Uint256(amount)),
                Collections.emptyList());

        String encodedFunction = FunctionEncoder.encode(function);
        BigInteger nonce = LightWallet.getNonce(fromAddress);

        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce,
                GAS_PRICE,
                GAS_LIMIT,
                contractAddress, encodedFunction);
        //签名Transaction，这里要对交易做签名
        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, LightWallet.buildCredentials(key));
        String hexValue = Numeric.toHexString(signMessage);
        //发送离线交易
        EthSendTransaction ethSendTransaction;
        try {
            ethSendTransaction = SpringContext.getBean(Web3j.class).ethSendRawTransaction(hexValue).send();
            System.out.println(JSON.toJSONString(ethSendTransaction));
        } catch (IOException e) {
            throw e;
        }
    }

    @Test
    public void testExistAddress(){

        //0x1348ef18771Cc3e43296dD4DAE22720708680375
        String address = "0x1348ef18771Cc3e43296dD4DAE22720708680375"; //这里是我的item  key是JedisKey.buildWalletAddressKey()
        System.out.println( IOCUtils._Get_Redis().hget(JedisKey.buildWalletAddressKey(),address));
    }

    @Test
    public void testExistAddressSet(){

        //0x1348ef18771Cc3e43296dD4DAE22720708680375
        String address = "0x8a5f7e444f0072b44198a8c32e5cc2c607c9a6a7"; //这里是我的item  key是JedisKey.buildWalletAddressKey()
        System.out.println( IOCUtils._Get_Redis().hHasKey(JedisKey.buildWalletAddressKey(),address));
    }

    @Test
    public void decodeInput() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String inputData = "0xb6ba5ae80000000000000000000000000000000000000000000000000000000000008481f8f559f96eeb8abed0de96155c3545855226d1b10b9a4a073360726c6615279c22ddafaf521412b39e3398371002be31620857ea0b5270601013b775e0cc6bad535f2a170585b35b606afff026dd5fbb3374b46e797a5808590602d1e662811f";
        String method = inputData.substring(0, 10);
        System.out.println(method);
        String to = inputData.substring(10, 74);
        String value = inputData.substring(74);
        Method refMethod = TypeDecoder.class.getDeclaredMethod("decode", String.class, int.class, Class.class);
        refMethod.setAccessible(true);
        Address address = (Address) refMethod.invoke(null, to, 0, Address.class);
        System.out.println(address.toString());
        Uint256 amount = (Uint256) refMethod.invoke(null, value, 0, Uint256.class);
        System.out.println(amount.getValue());
    }

    @Test
    public void testSyncBlock(){
        web3j.replayPastBlocksFlowable(new DefaultBlockParameterNumber(15516310),new DefaultBlockParameterNumber(15516320),true,true).subscribe(block -> {
            System.out.println(block.getRawResponse());
        });
    }

    @Test
    public void testGetBlockNumber() throws IOException {
        System.out.println(WalletUtils.getCurrentBlockNumber());
    }

    @Test
    public void testLastTimeBlockNumber(){
        System.out.println(WalletUtils.getSyncBlockNumber(20L));
    }
}
