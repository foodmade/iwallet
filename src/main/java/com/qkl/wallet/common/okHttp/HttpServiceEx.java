package com.qkl.wallet.common.okHttp;

import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.web3j.protocol.http.HttpService;

import java.util.concurrent.TimeUnit;

/**
 * @Author xiaom
 * @Date 2019/11/27 14:15
 * @Version 1.0.0
 * @Description <>
 **/
@Slf4j
public class HttpServiceEx extends HttpService {

    private static final Integer HTTP_TIME_OUT = 20;

    private static OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        configureLogging(builder);
        builder.retryOnConnectionFailure(true);
        log.info("---------设置Http超时时间--------{}秒",HTTP_TIME_OUT);
        builder.connectionPool(new ConnectionPool())
                .connectTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS).build();
        return builder.build();
    }

    public HttpServiceEx(String url) {
        this(url,createOkHttpClient(),false);
    }

    public HttpServiceEx(String url, OkHttpClient httpClient, boolean includeRawResponses) {
        super(url, httpClient, includeRawResponses);
    }

    private static void configureLogging(OkHttpClient.Builder builder) {
        if (log.isDebugEnabled()) {
            Logger var10002 = log;
            var10002.getClass();
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(var10002::debug);
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
        }
    }
}
