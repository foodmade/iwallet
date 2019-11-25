package com.qkl.wallet.common;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;

@Slf4j
public class WebSocketClientInst extends WebSocketClient {
    public WebSocketClientInst(URI serverUri ) {
        super( serverUri );
    }

    public WebSocketClientInst(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public WebSocketClientInst(URI serverUri, Draft draft, Map<String, String> headers, int connecttimeout) {
        super(serverUri, draft, headers, connecttimeout);
    }

    @Override
    public void onOpen( ServerHandshake handshakedata ) {
        System.out.println( "Connected" );

    }

    @Override
    public void onMessage( String message ) {
        System.out.println( "got: " + message );

    }

    @Override
    public void onClose( int code, String reason, boolean remote ) {
        System.out.println( "Disconnected" );
        System.exit( 0 );

    }

    @Override
    public void onError( Exception ex ) {
        ex.printStackTrace();

    }
}
