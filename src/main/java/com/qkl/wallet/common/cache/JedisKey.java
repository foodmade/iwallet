package com.qkl.wallet.common.cache;

import java.lang.management.BufferPoolMXBean;

/**
 * @Author xiaom
 * @Date 2019/11/21 18:36
 * @Version 1.0.0
 * @Description <>
 **/
public class JedisKey {

    public static final String CACHE_PREFIX = ":";

    private static final String PREFIX = "walletApp";

    //代币订单缓存key
    private static final String _CACHE_ORDER_TOKEN_QUEUE_KEY = "TOKEN";

    //提现订单的交易hash缓存key
    private static final String _WITHDRAW_TX_HASH_QUEUE = "WITHDRAW_TX_HASH";

    //所有钱包地址缓存key
    private static final String _WALLET_ADDRESS_KET = "_WALLET_ADDRESS_KET";

    //主链币订单缓存key
    private static final String _CACHE_ORDER_CHAIN_KEY = "CHAIN";

    //初始块高度
    public static final String _BASIS_BLOCK_NUMBER = "_BASIS_BLOCK_NUMBER";

    //项目基础配置文件
    public static final String _BASIS_CONFIG = "_BASIS_CONFIG";

    //最后一次同步的区块高度
    public static final String _LAST_TIME_BLOCK_NUMBER = "_LAST_TIME_BLOCK_NUMBER";

    //扫描区块确认数的hash队列
    public static final String _CONFIRM_SCAN_QUEUE = "_CONFIRM_SCAN_QUEUE";

    //交易的确认数详细信息缓存
    public static final String _CONFIRM_HASH_INFO = "_CONFIRM_HASH_INFO";

    //Token配置文件
    public static final String _TOKEN_CONFIG_KEY = "_TOKEN_CONFIG";

    /**
     * Build wallet order cache key.
     */
    public static String buildWalletOrderKey(String key){
        return buildKey("withdrawOrder",key);
    }

    public static String buildOrderLockKey(String key){
        return buildKey("lock",key);
    }

    /**
     * Withdraw order cache key.
     * @param key token Name.
     */
    public static String buildTokenOrderKey(String key){
        return buildKey(_CACHE_ORDER_TOKEN_QUEUE_KEY,key);
    }

    /**
     * Withdraw order
     * @return
     */
    public static String buildWithdrawTxHashKey(){
        return buildKey(_WITHDRAW_TX_HASH_QUEUE);
    }
    /**
     * 缓存订单key
     */
    public static String buildChainOrderKey(String key){
        return buildKey(_CACHE_ORDER_CHAIN_KEY,key);
    }

    /**
     * 从此钱包服务创建的所有钱包信息缓存key
     */
    public static String buildWalletAddressKey(){
        return buildKey(_WALLET_ADDRESS_KET);
    }

    private static String buildKey(Object str1, Object... array) {
        StringBuffer stringBuffer = new StringBuffer(PREFIX);
        stringBuffer.append(CACHE_PREFIX).append(str1);
        for (Object obj : array) {
            stringBuffer.append(CACHE_PREFIX).append(obj);
        }
        return stringBuffer.toString();
    }

}
