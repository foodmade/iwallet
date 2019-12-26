package com.qkl.wallet.common.walletUtil;

import com.alibaba.fastjson.JSON;
import com.qkl.wallet.common.Const;
import com.qkl.wallet.common.cache.JedisKey;
import com.qkl.wallet.common.cache.RedisUtil;
import com.qkl.wallet.common.SpringContext;
import com.qkl.wallet.common.enumeration.CallbackTypeEnum;
import com.qkl.wallet.common.enumeration.ExceptionEnum;
import com.qkl.wallet.common.exception.BadRequestException;
import com.qkl.wallet.common.tools.IOCUtils;
import com.qkl.wallet.common.tools.ReflectionUtils;
import com.qkl.wallet.config.Config;
import com.qkl.wallet.contract.Owc;
import com.qkl.wallet.core.ContractMapper;
import com.qkl.wallet.core.manage.OrderManage;
import com.qkl.wallet.core.manage.ScriptManage;
import com.qkl.wallet.domain.InputData;
import com.qkl.wallet.domain.RawTransactionResEntity;
import com.qkl.wallet.service.WalletService;
import com.qkl.wallet.service.impl.EventService;
import com.qkl.wallet.vo.in.WithdrawRequest;
import com.qkl.wallet.vo.out.CreateWalletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeDecoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * @Author Jackies
 * @Date 2019/12/14 17:02
 * @Description TODO://
 **/
@Slf4j
public class WalletUtils {

    /**
     * 发送离线交易
     * @param contractAddress   合约地址
     * @param toAddress         收款地址
     * @param amount            转账金额
     * @return 当前交易的nonce索引
     */
    public static RawTransactionResEntity offlineTransferToken(String contractAddress, String toAddress, BigInteger amount,String fromAddress,String secretKey) throws Exception {
        BigInteger GAS_PRICE =  SpringContext.getBean(WalletService.class).getGasPrice().getGasPrice();
        BigInteger GAS_LIMIT =  Const._GAS_LIMIT;

        Function function = new Function(
                Owc.FUNC_TRANSFER,
                Arrays.asList(new Address(toAddress), new Uint256(amount)),
                Collections.emptyList());

        String encodedFunction = FunctionEncoder.encode(function);
        BigInteger nonce = WalletUtils.getNonce(fromAddress);

        BigDecimal then = WalletUtils.unitEthCover(GAS_LIMIT.multiply(GAS_PRICE));

        log.info("Token transfer info. GAS_PRICE:[{}] GAS_LIMIT:[{}] GAS_TOTAL:[{}] nonce:[{}]",GAS_PRICE,GAS_LIMIT,then,nonce);

        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce,
                GAS_PRICE,
                GAS_LIMIT,
                contractAddress, encodedFunction);

        Credentials credentials = LightWallet.buildCredentials(secretKey);

        //签名Transaction，这里要对交易做签名
        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);

        String hexValue = Numeric.toHexString(signMessage);
        //发送离线交易
        EthSendTransaction ethSendTransaction;
        try {
            ethSendTransaction = SpringContext.getBean(Web3j.class).ethSendRawTransaction(hexValue).send();
        } catch (Exception e) {
            log.error("Offline transaction submission failed. ");
            throw e;
        }

        boolean status = IOCUtils.getWalletService().validTransferStatus(ethSendTransaction.getTransactionHash());

        if(ethSendTransaction.getTransactionHash() == null){
            String message = (ethSendTransaction.getError() == null ? "订单交易状态显示失败" : ethSendTransaction.getError().getMessage());
            log.error("SendTransaction token withdraw order failed. message:{}",message);
            //说明提交失败
            throw new Exception(message);
        }
        log.info("SendTransaction token order successful. status:[{}]. Response:[{}]",status, JSON.toJSONString(ethSendTransaction));
        return new RawTransactionResEntity(nonce,ethSendTransaction.getTransactionHash());
    }

    /**
     * 同步交易
     */
    public static void syncTransferToken(String tokenName,String toAddress, BigInteger amount,String trace){
        log.info("Start submitting a transfer request.");
        try {
            TransactionReceipt transactionReceipt = ContractMapper.get(tokenName)
                    .transfer(toAddress ,amount)
                    .send();
            log.info("Transfer order commit successful. TokenName:[{}] toAddress:[{}] amount:[{}]",tokenName,toAddress,amount);
            log.info("Transfer receipt info:[{}]",JSON.toJSONString(transactionReceipt));
            SpringContext.getBean(EventService.class).addSuccessEvent(transactionReceipt, new WithdrawRequest(toAddress,new BigDecimal(amount),trace));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void monitorNonceIsUpdate(BigInteger nonce, String fromAddress) {
        while (true){
            try {
                BigInteger uNonce = WalletUtils.getNonce(fromAddress);
                if(!nonce.equals(uNonce)){
                    break;
                }
                log.info("Current nonce:[{}] Chain block nonce:[{}]",nonce,uNonce);
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 缓存创建的钱包信息
     * @param walletInfo 钱包对象
     */
    public static void saveWalletInfo(CreateWalletResponse walletInfo) {
        IOCUtils._Get_Redis().hset(JedisKey.buildWalletAddressKey(),walletInfo.getAddress(),JSON.toJSONString(walletInfo));
    }

    /**
     * 检查钱包地址是否属于当前钱包服务创建
     * @param walletAddress ETH钱包地址
     */
    public static boolean validWalletAddress(String walletAddress){
        if(walletAddress == null){
            return false;
        }
        return IOCUtils._Get_Redis().hHasKey(JedisKey.buildWalletAddressKey(),walletAddress);
    }

    /**
     * 根据钱包地址获取钱包秘钥
     */
    public static String getKeySecretByAddress(String walletAddress){
        CreateWalletResponse detail = getWalletDetailByAddress(walletAddress);
        if(detail == null){
            return null;
        }
        return detail.getPrivateKey();
    }

    public static CreateWalletResponse getWalletDetailByAddress(String walletAddress){
        Object detailObj = IOCUtils._Get_Redis().hget(JedisKey.buildWalletAddressKey(),walletAddress);
        if(detailObj == null){
            return null;
        }
        return JSON.parseObject(detailObj.toString(),CreateWalletResponse.class);
    }

    /**
     * 单位换算 bigInt换算为eth
     */
    public static BigDecimal unitCover(BigInteger amount){
        return unitCover(new BigDecimal(amount));
    }

    public static BigDecimal unitCover(BigDecimal amount){
        return amount.divide(new BigDecimal(Const._TOKEN_UNIT),10,BigDecimal.ROUND_DOWN);
    }

    public static BigDecimal unitEthCover(BigInteger amount){
        return new BigDecimal(amount).divide(new BigDecimal(Const._ETH_TOKEN_UNIT),18,BigDecimal.ROUND_DOWN);
    }

    public static BigDecimal unitCover(BigInteger amount,String tokenName){
        return unitCover(new BigDecimal(amount),tokenName);
    }

    /**
     * 会通过tokenName查找对应的单位进行转换
     * @param amount  金额
     * @param tokenName  代币名称
     */
    public static BigDecimal unitCover(BigDecimal amount,String tokenName){
        if(tokenName == null){
            return amount;
        }
        Long decimals = IOCUtils.getWalletService().foundDecimalsByTokenName(tokenName);
        return amount.divide(new BigDecimal(decimals),8,BigDecimal.ROUND_DOWN);
    }

    /**
     * 获取以太坊当前区块高度
     */
    public static Long getCurrentBlockNumber() throws IOException {
        try {
            return IOCUtils.getWeb3j().ethBlockNumber().send().getBlockNumber().longValue();
        } catch (IOException e) {
            log.error("获取以太坊最新区块高度失败,错误信息:[{}]",e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取交易随机数
     */
    public static BigInteger getNonce(String address){
        EthGetTransactionCount ethGetTransactionCount;
        try {
            ethGetTransactionCount = IOCUtils.getWeb3j().ethGetTransactionCount(
                    address, DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            return nonce;
        } catch (Exception e) {
            log.error("获取交易随机数(nonce)异常 message:{}",e.getMessage());
            return null;
        }

    }

    /**
     * 获取钱包服务初始区块高度
     */
    public static BigInteger getBasisBlockNumber(RedisUtil redisUtil){
        Object val = redisUtil.get(JedisKey._BASIS_BLOCK_NUMBER);
        return val == null ? null : new BigInteger(val + "");
    }

    public static void generateBasisBlockNumber(Long blockNumber){
        IOCUtils._Get_Redis().set(JedisKey._BASIS_BLOCK_NUMBER,blockNumber.toString());
    }

    /**
     * 初始化项目配置到redis
     */
    public static void initBasisConfig(Config config, RedisUtil redisUtil) {
        Assert.notNull(config,"项目基础配置文件存在异常,请检查");
        redisUtil.set(JedisKey._BASIS_CONFIG,JSON.toJSONString(config));
    }

    /**
     * 获取系统配置,根据key
     * @param key  配置key
     * @param cls  需要转换的类定义
     * @param <T>  返回值泛型
     */
    public static <T> T getConfigValByKey(String key,Class<T> cls){
        Object val = IOCUtils._Get_Redis().get(JedisKey._BASIS_CONFIG);
        if(val == null){
            return null;
        }
        Config config = JSON.parseObject(JSON.toJSONString(val),Config.class);
        Object keyVal = ReflectionUtils.getFieldValue(config,key);
        if(keyVal == null){
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(key),cls);
    }

    /**
     * 获取需要同步的区块高度
     * 如果lastTimeBlockNumber + BlockInterval > currentBlockNumber时,将会返回null,说明无法获取合适的区块高度,等待主链提交新的区块.
     * @param currentBlockNumber 当前区块链最新高度
     */
    public static Long getSyncBlockNumber(Long currentBlockNumber){
        Long number = IOCUtils._Get_Redis().getRedisTemplate().execute(ScriptManage.getInstance().getLastBlockNumberCallScript(),
                new ArrayList<>(),IOCUtils.getConfig().getBlockInterval().toString(),currentBlockNumber.toString());
        if(number == null){
            return null;
        }
        return number;
    }

    /**
     * 设置已同步的区块结束块位置
     */
    public static void setEndSyncBlockNumber(BigInteger endBlockNumber){
        IOCUtils._Get_Redis().set(JedisKey._LAST_TIME_BLOCK_NUMBER,endBlockNumber.toString());
    }

    /**
     * 解析Transaction input data.
     */
    public static InputData decodeInput(String inputData) throws Exception {
        String method = inputData.substring(0, 10);
        String to = inputData.substring(10, 74);
        String value = inputData.substring(74);
        Method refMethod = TypeDecoder.class.getDeclaredMethod("decode", String.class, int.class, Class.class);
        refMethod.setAccessible(true);
        Address address = (Address) refMethod.invoke(null, to, 0, Address.class);
        Uint256 amount = (Uint256) refMethod.invoke(null, value, 0, Uint256.class);
        return new InputData(method,amount.getValue(),address.toString());
    }

    /**
     * 验证ETH钱包地址是否合法
     */
    public static boolean isETHValidAddress(String input) {
        if (StringUtils.isEmpty(input) || !input.startsWith("0x"))
            return false;
        return isValidAddress(input);
    }

    public static boolean isValidAddress(String input) {
        String cleanInput = Numeric.cleanHexPrefix(input);

        try {
            Numeric.toBigIntNoPrefix(cleanInput);
        } catch (NumberFormatException e) {
            return false;
        }

        return cleanInput.length() == 40;
    }

    /**
     * 检查当前交易属于充值还是提现
     */
    public static CallbackTypeEnum judgeOrderType(String transactionHash) {
        if(org.apache.commons.lang3.StringUtils.isBlank(transactionHash)){
            throw new BadRequestException(ExceptionEnum.PARAMS_MISS_ERR);
        }
        if(OrderManage.isWithdrawExist(transactionHash)){
            return CallbackTypeEnum.WITHDRAW_TYPE;
        }else{
            return CallbackTypeEnum.RECHARGE_TYPE;
        }
    }
}
