package com.qkl.wallet.common.task;

import com.alibaba.fastjson.JSON;
import com.qkl.wallet.config.ApplicationConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;

import java.util.Arrays;

@Slf4j
public class ListenerTransferEvent extends Thread {

    private Web3j web3j;

    public ListenerTransferEvent(Web3j web3j) {
        this.web3j = web3j;
    }


    @Override
    public void run() {

        Credentials.create(ApplicationConfig.secretKey);

        Event event = new Event("transfer",
                Arrays.asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));

        EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST,
                DefaultBlockParameterName.LATEST, ApplicationConfig.contractAddress);

        filter.addSingleTopic(EventEncoder.encode(event));

        web3j.transactionFlowable().subscribe(monitor -> {
            log.info("Monitor transfer event ：\n");
            log.info(JSON.toJSONString(monitor));
        });

        log.info("Contract event monitoring started successfully.");
    }
}
