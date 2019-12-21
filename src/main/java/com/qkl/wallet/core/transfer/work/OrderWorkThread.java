package com.qkl.wallet.core.transfer.work;

import com.qkl.wallet.common.RedisUtil;
import com.qkl.wallet.common.SpringContext;
import com.qkl.wallet.common.enumeration.TokenEventEnum;
import com.qkl.wallet.core.manage.OrderManage;
import com.qkl.wallet.domain.OrderModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

import java.lang.reflect.Constructor;

/**
 * @Author Jackies
 * @Date 2019/12/15 17:21
 * @Description TODO:// 处理交易的工作线程
 **/
@Slf4j
public class OrderWorkThread extends Thread {

    protected String tokenName;

    protected TokenEventEnum event;

    protected boolean isRunning = false;

    protected RedisUtil redisUtil;

    public OrderWorkThread(String tokenName, TokenEventEnum eventEnum, RedisUtil redisUtil) {
        this.tokenName = tokenName;
        this.event = eventEnum;
        this.redisUtil = redisUtil;
    }

    public OrderModel onProcess(){
        return OrderManage.lpopOrderForEntity(redisUtil,tokenName, OrderModel.class);
    }

    public void sleep(){
        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        log.info("Active current thread. Token name: [{}]. Start listener.....",tokenName);
        while (true){
            if(isRunning){
                log.info("Work thread is running. ThreadName:[{}]",tokenName);
                sleep();
                continue;
            }
            OrderModel order = onProcess();
            if(order == null){
                sleep();
                continue;
            }
            pause();
            log.info("Accept order manage withdraw order. createTime:[{}], retry:[{}] trace:[{}]",order.getTime(),order.getRetry(),order.getWithdraw().getTrace());
            ApplicationEvent event = installEvent(order);
            if(event == null){
                log.error("newInstall event model failed. tokenName:[{}]",tokenName);
                sleep();
                continue;
            }
            SpringContext.getApplicationContext().publishEvent(event);
            sleep();
        }
    }

    private ApplicationEvent installEvent(OrderModel order){
        try {
            Constructor constructor = event.getaClass().getConstructor(Object.class,OrderModel.class);
            return (ApplicationEvent)constructor.newInstance(this,order);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void pause(){
        this.isRunning = true;
    }

    public void play(){
        this.isRunning = false;
    }
}
