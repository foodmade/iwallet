package com.qkl.wallet.core.transfer.work;

import com.qkl.wallet.common.SpringContext;
import com.qkl.wallet.common.tools.IOCUtils;
import com.qkl.wallet.common.walletUtil.WalletUtils;
import com.qkl.wallet.config.Config;
import com.qkl.wallet.core.event.EthTransactionEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @Author Jackies
 * @Date 2019/12/18 10:40
 * @Description TODO:// 监听以太坊交易区块的工作线程
 **/
@Slf4j
public class TransactionMonitorThread extends Thread{

    //线程名称
    private static final String threadName = "TransactionMonitorThread";
    //获取区块延迟个数
    private static Integer blockInterval = IOCUtils.getConfig().getBlockInterval();

    public TransactionMonitorThread() {
        super(threadName);
    }

    /**
     * 1 首次执行时,需要获取初始区块高度 {@link WalletUtils#getCurrentBlockNumber()}
     * 2 获取延迟区块高度 {@link WalletUtils#getConfigValByKey(String, Class)}  String ==> delayBlockNumber
     * 3 获取上次同步区块的高度
     * 4 以上次同步的区块高度(startBlockNumber)为起点高度, (endBlockNumber = startBlockNumber + {@link Config#getBlockInterval()})为结束高度进行监听区块内所有的交易
     * 5 交易处理完毕后,将endBlockNumber以原子性方式存入redis,防止多台服务器出现的脏读现象
     * 6 如果在同步区块时出现异常,需要将startBlockNumber和endBlockNumber写入redis失败队列,以供后面问题查询
     */
    @Override
    public void run() {
        log.info("Start Transaction monitor handler thread work.");
        while (true){
            try {
                monitor();
                sleep();
            }catch (Exception e){
                log.error("Transaction monitor handler thread execute failed. throw message:[{}]",e.getMessage());
                sleep();
            }
        }
    }

    private void monitor() {
        try {
            Long currentBlockNumber = WalletUtils.getCurrentBlockNumber();
            if(currentBlockNumber == null){
                return;
            }
            Long lastBlockNumber = WalletUtils.getSyncBlockNumber(currentBlockNumber);
            if(lastBlockNumber == null){
                log.warn("LastTimeBlockNumber is null. Please check redis config.");
                return;
            }

            long endBlockNumber = lastBlockNumber + blockInterval;

            //获取最新区块高度,如果lastBlockNumber + blockInterval >= currentBlockNumber,则说明已经完成所有同步,结束执行.
            if(endBlockNumber > currentBlockNumber){
                log.debug("Already the latest block, no synchronization required......");
                return;
            }
            log.info("Fetch eth transaction order. \n currentBlockNumber:[{}] \n lastBlockNumber:[{}] \n endBlockNumber:[{}]",currentBlockNumber,lastBlockNumber,endBlockNumber);
            //执行区块同步逻辑,获取交易详情
            SpringContext.getApplicationContext().publishEvent(new EthTransactionEvent(this,lastBlockNumber,endBlockNumber));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sleep(){
        try {
            sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
