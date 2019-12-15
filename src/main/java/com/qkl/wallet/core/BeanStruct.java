package com.qkl.wallet.core;

import com.alibaba.fastjson.JSON;
import com.qkl.wallet.common.okHttp.HttpServiceEx;
import com.qkl.wallet.common.tools.JsonReadUtils;
import com.qkl.wallet.config.ApplicationConfig;
import com.qkl.wallet.config.TokenConfigs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;

import java.io.IOException;
import java.net.URL;

@Slf4j
@Component
public class BeanStruct {

    @Autowired
    private ApplicationConfig applicationConfig;

    /**
     * web3j client bean init.
     * @return
     */
    @Bean
    @Order(1)
    public Web3j initWeb3jClient() {
        Web3j web3j = Web3j.build(new HttpServiceEx(ApplicationConfig.blockHost));
        Web3ClientVersion web3ClientVersion;
        try {
            web3ClientVersion = web3j.web3ClientVersion().send();
            String clientVersion = web3ClientVersion.getWeb3ClientVersion();
            log.info("Web3j version info \t\t >>> {}" , clientVersion);
            log.info("Connected server address is >>> {}" , ApplicationConfig.blockHost);
            log.info("Web3j client initialization finish.");
        } catch (Exception e) {
            log.error("Fetch web3j version throw err:[{}]",e.getMessage());
            e.printStackTrace();
        }
        return web3j;
    }

    @Bean
    public TokenConfigs readTokenJsonConfig() throws IOException {
        try {
            TokenConfigs tokenConfigs = JsonReadUtils.readJsonFromClassPath("token.json", TokenConfigs.class);
            log.info("Token json load successful. \n Token json:[{}]", JSON.toJSONString(tokenConfigs));
            return tokenConfigs;
        } catch (IOException e) {
            log.error("Serious warning::::: \t Already found token json file. But read this file failed. throw error message:[{}]",e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
