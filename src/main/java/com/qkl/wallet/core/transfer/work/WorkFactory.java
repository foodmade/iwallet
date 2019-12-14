package com.qkl.wallet.core.transfer.work;

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

    public Optional<Thread> buildThreadWork(String tokenName){
        return Optional.of(new WorkThread(tokenName));
    }

}
