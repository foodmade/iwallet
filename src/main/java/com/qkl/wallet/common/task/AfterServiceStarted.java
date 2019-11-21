package com.qkl.wallet.common.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;

@Component
@Slf4j
public class AfterServiceStarted implements ApplicationRunner {

    @Autowired
    private Web3j web3j;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //Start transfer event monitor work thread.
//        new ListenerTransferEvent(web3j).start();
    }
}
