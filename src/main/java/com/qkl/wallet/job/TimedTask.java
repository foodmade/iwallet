package com.qkl.wallet.job;

import com.alibaba.fastjson.JSON;
import com.qkl.wallet.common.JedisKey;
import com.qkl.wallet.common.RedisUtil;
import com.qkl.wallet.common.enumeration.CallbackTypeEnum;
import com.qkl.wallet.common.walletUtil.WalletUtils;
import com.qkl.wallet.config.Config;
import com.qkl.wallet.domain.Confirm;
import com.qkl.wallet.service.impl.EventService;
import com.qkl.wallet.vo.out.WithdrawCallback;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Jackies
 * @Date 2019/12/18 12:05
 * @Description TODO://
 **/
@Component
@Slf4j
public class TimedTask {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private Config config;
    @Autowired
    private EventService eventService;

    @Scheduled(fixedDelay = 1000 * 10)
    public void confirmBlockTimer(){
        log.info("Start execute confirm block number timer task. ");
        try {
            //获取任务队列中所有待确认区块数的hash
            List<String> allHash = popAllHash();
            if(allHash.isEmpty()){
                return;
            }
            Long currentBlockNumber = WalletUtils.getCurrentBlockNumber();
            Confirm confirm;
            for (String hash : allHash) {
                confirm = getHashConfirm(hash);
                if(confirm == null || !confirm.getStatus()){
                    continue;
                }
                confirm.setConfirmBlockCnt(currentBlockNumber - confirm.getBlockNumber());
                if(confirm.getConfirmBlockCnt() > config.getConfirmMaxCount()){
                    //说明已达到最大确认数,提交确认信息
                    processConfirmNum(confirm);
                    destroyInfo(confirm);
                }
            }
        } catch (IOException e) {
            log.error("Confirm scan timerTask execute failed. message:{}",e.getMessage());
            e.printStackTrace();
        }finally {
            log.info("Execute confirm block number timer task finish. ");
        }
    }

    private void destroyInfo(Confirm confirm) {
        removeHash(confirm.getHash());
        cacheNewConfirmInfo(confirm);
    }

    private void processConfirmNum(Confirm confirm){
        WithdrawCallback callback = new WithdrawCallback(CallbackTypeEnum.CONFIRM_TYPE);
        callback.setConfirmBlockNumber(confirm.getConfirmBlockCnt());
        callback.setTxnHash(confirm.getHash());
        eventService.addSuccessEvent(callback);
    }

    private void cacheNewConfirmInfo(Confirm confirm){
        redisUtil.hset(JedisKey._CONFIRM_HASH_INFO,confirm.getHash(),JSON.toJSONString(confirm));
    }

    private List<String> popAllHash(){
        return new ArrayList<>(redisUtil.sGet(JedisKey._CONFIRM_SCAN_QUEUE));
    }

    private void removeHash(String hash){
        redisUtil.sDelKey(JedisKey._CONFIRM_SCAN_QUEUE,hash);
    }

    private Confirm getHashConfirm(String hash){
        Object obj = redisUtil.hget(JedisKey._CONFIRM_HASH_INFO,hash);
        if(obj == null){
            return null;
        }
        return JSON.parseObject(obj.toString(),Confirm.class);
    }
}
