package com.qkl.wallet.common.task;

import com.alibaba.fastjson.JSON;
import com.qkl.wallet.common.JedisKey;
import com.qkl.wallet.common.RedisUtil;
import com.qkl.wallet.common.SpringContext;
import com.qkl.wallet.common.walletUtil.LightWallet;
import com.qkl.wallet.contract.Token;
import com.qkl.wallet.service.impl.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.websocket.WebSocketClient;
import org.web3j.protocol.websocket.WebSocketService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ListenerTransferEvent extends Thread {

    private Web3j web3j;

    public ListenerTransferEvent(Web3j web3j) {
        this.web3j = web3j;
    }


    @Override
    public void run() {
        monitorTransferEvent();
    }



    private void monitorTransferEvent(){

        Token token = LightWallet.loadTokenClient(web3j);
        Event event = new Event("Transfer",
                Arrays.asList(
                        new TypeReference<Address>() {
                        },
                        new TypeReference<Address>() {
                        },
                        new TypeReference<Uint256>(){}));
        final EthFilter ethFilter = new EthFilter(
                DefaultBlockParameterName.EARLIEST,
                DefaultBlockParameterName.LATEST,
                token.getContractAddress());
        ethFilter.addSingleTopic(EventEncoder.encode(event));
        token.transferEventFlowable(ethFilter)
                .subscribe(monitor -> {
                    log.info("Monitor transfer event:{}",JSON.toJSONString(monitor));
                    try {
                        if(!lockTxHash(monitor.log.getTransactionHash())){
                            log.debug("Current trading order has been submitted.<><><><><><>");
                            return;
                        }
                        addEvent(monitor);
                    }catch (Exception e){
                        log.error(e.getMessage());
                    }
                });
    }

    private boolean lockTxHash(String transactionHash) {
        RedisUtil redisUtil = SpringContext.getBean(RedisUtil.class);
        return redisUtil.tryGetLock(JedisKey.buildOrderLockKey(transactionHash),"1");
    }

    private void addEvent(Token.TransferEventResponse eventResponse) {
        Assert.notNull(eventResponse, "Monitor event response data err:: Because this object is null");
        Assert.notNull(eventResponse.to, "Monitor event response data err:: Because toAddress is null");
        EventService eventService = SpringContext.getApplicationContext().getBean(EventService.class);
        Assert.notNull(eventService, "Failed to get [eventService] from SpringContext :::: Please restart server.");

        eventService.addSuccessEvent(eventResponse);
    }
}
