package com.qkl.wallet.core.event;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

import java.math.BigInteger;

/**
 * @Author Jackies
 * @Date 2019/12/18 12:16
 * @Description TODO://
 **/
@Data
public class MonitorTransactionEvent extends ApplicationEvent {

    private Long startBlockNumber;

    private Long endBlockNumber;

    public MonitorTransactionEvent(Object source, Long startBlockNumber, Long endBlockNumber) {
        super(source);
        this.startBlockNumber = startBlockNumber;
        this.endBlockNumber = endBlockNumber;
    }
}
