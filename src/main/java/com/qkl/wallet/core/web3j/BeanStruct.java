package com.qkl.wallet.core.web3j;

import com.alibaba.fastjson.JSONObject;
import com.qkl.wallet.config.ApplicationConfig;
import io.netty.util.concurrent.DefaultEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.core.methods.response.management.AdminNodeInfo;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.ipc.UnixIpcService;
import org.web3j.protocol.websocket.WebSocketClient;
import org.web3j.protocol.websocket.WebSocketService;
import org.web3j.protocol.websocket.events.NewHeadsNotification;
import org.web3j.protocol.websocket.events.PendingTransactionNotification;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

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
    public Web3j initWeb3jClient() throws Exception {


//        WebSocketClient webSocketClient = new WebSocketClient(new URI("wss://mainnet.infura.io/ws/v3/2a9a4f645bf64a378b5352497557e0db"));
//
//        webSocketClient.connectBlocking();
//        WebSocketService service = new WebSocketService(webSocketClient,true);
//
//        Request<?, Web3ClientVersion> request = new Request<>(
//                "web3_clientVersion",
//                Collections.<String>emptyList(),
//                service,
//                Web3ClientVersion.class);
//
//        service.subscribe(request,"eth_newPendingTransactionFilter", PendingTransactionNotification.class);
//
//        Web3j web3j = Web3j.build(service);
        Web3j web3j = Web3j.build(new HttpService(ApplicationConfig.blockHost));

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
}
