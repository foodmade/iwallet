package com.qkl.wallet.common.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qkl.wallet.common.WebSocketClientInst;
import com.qkl.wallet.common.WebsocketClientEndpoint;
import com.qkl.wallet.config.ApplicationConfig;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ListenerTransferEvent extends Thread {

    private Web3j web3j;

    public ListenerTransferEvent(Web3j web3j) {
        this.web3j = web3j;
    }


    @Override
    public void run() {

//        Credentials.create(ApplicationConfig.secretKey);
/*        Event event = new Event("transfer",
                Arrays.asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));*/

/*        EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST,
                DefaultBlockParameterName.LATEST, ApplicationConfig.contractAddress);

        filter.addSingleTopic(EventEncoder.encode(event));*/
        Event event = new Event("Transfer",
                Arrays.asList(
                        new TypeReference<Address>() {
                        },
                        new TypeReference<Address>() {
                        },new TypeReference<Uint256>(){}));

        EthFilter filter = new EthFilter(
                DefaultBlockParameterName.LATEST,
                DefaultBlockParameterName.LATEST,
                ApplicationConfig.contractAddress);
        filter.addSingleTopic(EventEncoder.encode(event));


//        web3j.ethLogObservable(filter).subscribe(monitor -> {
//            System.out.println("ethLogObservable:" + JSON.toJSONString(monitor));
//        });
//
        web3j.transactionFlowable().subscribe(monitor -> {
            log.info("Monitor transfer event ：\n");
            log.info(JSON.toJSONString(monitor));
        });

        log.info("Contract event monitoring started successfully.<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>");
    }

    private void testWebsocket() throws Exception {
            new Thread(() ->{
                    // open websocket
                    final WebsocketClientEndpoint clientEndPoint;
                    try {
                        clientEndPoint = new WebsocketClientEndpoint(new URI("wss://mainnet.infura.io/ws"));
                        // add listener
                        clientEndPoint.addMessageHandler(message -> System.out.println("Handler:"+message));
                        // send message to websocket
                        clientEndPoint.sendMessage("{'jsonrpc':'2.0','method':'eth_newFilter','params':[],'id':1}");
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
            }).start();
    }

    private void testWebSocket2() throws Exception {
        WebSocketClient mWs = new WebSocketClient( new URI( "wss://mainnet.infura.io/ws" ), new Draft_10() )
        {
            @Override
            public void onMessage( String message ) {
                JSONObject obj = JSON.parseObject(message);
                String channel = obj.getString("channel");
                System.out.println("接受到消息：" +channel);
            }

            @Override
            public void onOpen( ServerHandshake handshake ) {
                System.out.println( "opened connection" );
            }

            @Override
            public void onClose( int code, String reason, boolean remote ) {
                System.out.println( "closed connection" );
            }

            @Override
            public void onError( Exception ex ) {
                ex.printStackTrace();
            }

        };
        //open websocket
        mWs.connect();
        JSONObject obj = new JSONObject();
        obj.put("event", "addChannel");
        obj.put("channel", "ok_btccny_ticker");
        String message = obj.toString();
        //send message
        mWs.send(message);
    }
}
