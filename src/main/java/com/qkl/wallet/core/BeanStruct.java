package com.qkl.wallet.core;

import com.alibaba.fastjson.JSON;
import com.qkl.wallet.common.SpringContext;
import com.qkl.wallet.common.cache.JedisKey;
import com.qkl.wallet.common.cache.RedisUtil;
import com.qkl.wallet.common.okHttp.HttpServiceEx;
import com.qkl.wallet.common.tools.JsonReadUtils;
import com.qkl.wallet.config.ApplicationConfig;
import com.qkl.wallet.config.Config;
import com.qkl.wallet.config.TokenConfigs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;

import java.io.IOException;

@Slf4j
@Component
public class BeanStruct {

    @Autowired
    private ApplicationConfig applicationConfig;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * web3j client bean init.
     */
    @Bean
    @DependsOn("tokenConfigs")
    public Web3j initWeb3jClient() {
        String ethHost = ((TokenConfigs)SpringContext.getBean("tokenConfigs")).getEthPlatformHost();
        Web3j web3j = Web3j.build(new HttpServiceEx(ethHost));
        Web3ClientVersion web3ClientVersion;
        try {
            web3ClientVersion = web3j.web3ClientVersion().send();
            String clientVersion = web3ClientVersion.getWeb3ClientVersion();
            log.info("Web3j version info \t\t >>> {}" , clientVersion);
            log.info("Connected server address is >>> {}" , ethHost);
            log.info("Web3j client initialization finish.");
        } catch (Exception e) {
            log.error("Fetch web3j version throw err:[{}]",e.getMessage());
            e.printStackTrace();
        }
        return web3j;
    }

    @Bean("tokenConfigs")
    @Order(1)
    public TokenConfigs readTokenJsonConfig() {
        try {
            //Token配置文件改造为放入redis
            Object tokenObj = redisUtil.get(JedisKey._TOKEN_CONFIG_KEY);
            TokenConfigs tokenConfigs = JSON.parseObject(tokenObj.toString(),TokenConfigs.class);
            log.info("Token json load successful. \n Token json:[{}]", JSON.toJSONString(tokenConfigs));
            return tokenConfigs;
        } catch (Exception e) {
            log.error("Serious warning::::: \t Already found token json file. But read this file failed. throw error message:[{}]",e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Bean
    public Config readConfigJsonConfig() throws IOException {
        try {
            Config config = JsonReadUtils.readJsonFromClassPath("config.json", Config.class);
            log.info("Config json load successful. \n Config json:[{}]", JSON.toJSONString(config));
            return config;
        } catch (IOException e) {
            log.error("Serious warning::::: \t Already found Config json file. But read this file failed. throw error message:[{}]",e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
