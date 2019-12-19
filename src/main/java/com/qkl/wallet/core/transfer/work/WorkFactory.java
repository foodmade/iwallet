package com.qkl.wallet.core.transfer.work;

import com.qkl.wallet.common.enumeration.TokenEventEnum;

import java.util.Optional;

/**
 * @Author Jackies
 * @Date 2019/12/14 13:47
 * @Description TODO://
 **/
public class WorkFactory {

    public static WorkFactory build(){
        return new WorkFactory();
    }

    /**
     * 构建代币类型的工作线程
     * @param tokenName 代币名称
     * @param eventEnum 线程事件枚举
     */
    public Optional<Thread> buildTokenThreadWork(String tokenName, TokenEventEnum eventEnum){
        return Optional.of(new OrderWorkThread(tokenName,eventEnum));
    }

}
