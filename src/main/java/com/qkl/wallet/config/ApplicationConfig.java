package com.qkl.wallet.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * @Author xiaom
 * @Date 2019/11/20 16:29
 * @Version 1.0.0
 * @Description <>
 **/
@ConfigurationProperties
@Component
public class ApplicationConfig {
    /**
     * 合约地址
     */
    public static String contractAddress;
    /**
     * 链地址
     */
    public static String blockHost;
    /**
     * 秘钥
     */
    public static String secretKey;

    /**
     * keystore保存路径
     */
    public static String wallPath;
    /**
     * 钱包默认密码
     */
    public static String defaultPassword;

    @Value("${wallet.contract.address}")
    public void setContractAddress(String address){
        ApplicationConfig.contractAddress = address;
    }

    @Value("${wallet.server.host}")
    public void setBlockHost(String host){
        ApplicationConfig.blockHost = host;
    }

    @Value("${wallet.account.secretKey}")
    public void setSecret(String secretKey){
        ApplicationConfig.secretKey = secretKey;
    }

    @Value("${wallet.keystore.path}")
    public void setWallPath(String path){
        ApplicationConfig.wallPath = path;
    }

    @Value("${wallet.password}")
    public void setDefaultPassword(String password){
        ApplicationConfig.defaultPassword = password;
    }

}
