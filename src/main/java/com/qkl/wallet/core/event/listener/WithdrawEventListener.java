package com.qkl.wallet.core.event.listener;

import com.alibaba.fastjson.JSON;
import com.qkl.wallet.common.BeanUtils;
import com.qkl.wallet.common.HttpUtils;
import com.qkl.wallet.common.RedisUtil;
import com.qkl.wallet.common.enumeration.Status;
import com.qkl.wallet.config.ApplicationConfig;
import com.qkl.wallet.core.event.WithdrawEvent;
import com.qkl.wallet.service.TransactionManageService;
import com.qkl.wallet.vo.out.WithdrawCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class WithdrawEventListener {

    @Autowired
    private TransactionManageService transactionManageService;

    private static final String callbackUrl = ApplicationConfig.callBackHost + "/admin/financeManage/callbackWallet";

    @EventListener
    public void onApplicationEvent(WithdrawEvent event) {
        try {
            log.info("Received an event call. Status:[{}] message:[{}]",event.getType(),event.getMessage());
            Optional<Status> statusOptional = Status.find(event.getType());
            if(!statusOptional.isPresent()){
                log.error("Abnormal event type:[{}]",event.getType());
                return;
            }
            log.info("Event type:[{}] message:[{}] Callback object:{}",event.getType(),event.getMessage(),event.getCallbackResponse());
            processCallback(event);
            transactionManageService.clearTransactionOrderCache(event.getCallbackResponse().getRecepient());
            log.info("Callback notification event execution completed >>>>>>>>><<<<<<<<<<<<<<<");
        }catch (Exception e){
            log.error("Process callback server throw error >>>>>>>>><<<<<<<<<<<<<<<");
            log.error("throw error info:{}",e.getMessage());
            e.printStackTrace();
        }
    }

    private void processCallback(WithdrawEvent event) throws Exception {
        log.info("Start process http request in ECC server.");
        log.info("Request body info:{}",JSON.toJSONString(event));

        ResponseEntity responseEntity =  HttpUtils.postForEntity(callbackUrl,parserMap(event.getCallbackResponse()));

        log.info("Process ECC server finish. Response info:[{}]",JSON.toJSONString(responseEntity));
    }

    private Map parserMap(WithdrawCallback request) throws Exception {
        return BeanUtils.convertBean(request);
    }

}
