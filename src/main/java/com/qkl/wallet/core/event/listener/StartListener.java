package com.qkl.wallet.core.event.listener;

import com.qkl.wallet.config.ChainConfiguration;
import com.qkl.wallet.config.InitParamsConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @Author xiaom
 * @Date 2019/12/25 10:24
 * @Version 1.0.0
 * @Description <>
 **/
@Component
@Slf4j
public class StartListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ChainConfiguration chainConfiguration;
    @Autowired
    private InitParamsConfiguration initParamsConfiguration;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("Spring servlet start finish. Execute project initialization");
        try {
            initParamsConfiguration.initParamsConfig();
            chainConfiguration.initializationContractConfiguration();
        } catch (Exception e) {
            log.error("Initialization failed, Message:[{}]",e.getMessage());
            e.printStackTrace();
        }
    }
}
