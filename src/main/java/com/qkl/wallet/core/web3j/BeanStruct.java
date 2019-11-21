package com.qkl.wallet.core.web3j;

import com.qkl.wallet.config.ApplicationConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;

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
    public Web3j initWeb3jClient(){
        Web3j web3j = Web3j.build(new HttpService(ApplicationConfig.blockHost));

        Web3ClientVersion web3ClientVersion;
        try {
            web3ClientVersion = web3j.web3ClientVersion().send();
            String clientVersion = web3ClientVersion.getWeb3ClientVersion();
            log.info("Web3j version info \t\t >>> {}" , clientVersion);
            log.info("Connected server address is >>> {}" , ApplicationConfig.blockHost);
            log.info("Web3j client initialization finish.");
        } catch (IOException e) {
            log.error("Fetch web3j version throw err:[{}]",e.getMessage());
            e.printStackTrace();
        }
        return web3j;
    }
}
