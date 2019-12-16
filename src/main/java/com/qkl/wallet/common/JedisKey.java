package com.qkl.wallet.common;

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
