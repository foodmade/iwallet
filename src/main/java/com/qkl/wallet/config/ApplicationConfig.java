package com.qkl.wallet.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

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
     * keystore保存路径
     */
    public static String wallPath;
    /**
     * 钱包默认密码
     */
    public static String defaultPassword;

    /**
     * 连接超时时间
     */
    public static Integer connectionTimeout;

    /**
     * 信息读取超时时间
     */
    public static Integer readTimeout;
    /**
     * 回调地址
     */
    public static String callBackHost;

    @Value("${wallet.keystore.path}")
    public void setWallPath(String path){
        ApplicationConfig.wallPath = path;
    }

    @Value("${wallet.password}")
    public void setDefaultPassword(String password){
        ApplicationConfig.defaultPassword = password;
    }

    @Value("${http.connection.timeout}")
    public void setConnectionTimeout(Integer connectionTimeout) {
        ApplicationConfig.connectionTimeout = connectionTimeout;
    }

    @Value("${http.read.timeout}")
    public void setReadTimeout(Integer readTimeout) {
        ApplicationConfig.readTimeout = readTimeout;
    }

    @Value("${host.callback}")
    public void setCallBackHost(String callBackHost) {
        ApplicationConfig.callBackHost = callBackHost;
    }
}
