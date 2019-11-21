package com.qkl.wallet.common;

/**
 * @Author xiaom
 * @Date 2019/11/21 18:36
 * @Version 1.0.0
 * @Description <>
 **/
public class JedisKey {

    public static final String CACHE_PREFIX = ":";

    private static final String PREFIX = "walletApp";

    /**
     * Build wallet order cache key.
     */
    public static String buildWalletOrderKey(String key){
        return buildKey("withdrawOrder",key);
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
