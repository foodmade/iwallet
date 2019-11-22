package com.qkl.wallet.core.event.listener;

import com.alibaba.fastjson.JSON;
import com.qkl.wallet.core.event.WithdrawEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class WithdrawEventListener {

    @Autowired
    private RestTemplate restTemplate;

    @EventListener
    public void onApplicationEvent(WithdrawEvent event) {
        log.info("接收到转账完成的事件消息....");
        log.info("状态:{}",event.getType());
        log.info("消息:{}",event.getMessage());
        log.info(">>>>{}",JSON.toJSONString(event.getWithdrawRequest()));
    }
}
