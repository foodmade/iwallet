package com.qkl.wallet.core.transfer.work;

import com.qkl.wallet.common.SpringContext;
import com.qkl.wallet.core.event.TransferEvent;
import com.qkl.wallet.core.transfer.OrderManage;
import com.qkl.wallet.core.transfer.OrderModel;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author Jackies
 * @Date 2019/12/14 13:49
 * @Description TODO://
 **/
@Slf4j
public class WorkThread extends Thread{

    /**
     * Token name.
     */
    private String tokenName;

    private boolean isRunning = false;

    public WorkThread(String tokenName) {
        this.tokenName = tokenName;
    }

    public OrderModel onProcess(){
        return OrderManage.lpopOrderForEntity(tokenName, OrderModel.class);
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
            SpringContext.getApplicationContext().publishEvent(new TransferEvent(this,order));
            sleep();
        }
    }

    public void pause(){
        this.isRunning = true;
    }

    public void play(){
        this.isRunning = false;
    }

    private void sleep(){
        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
