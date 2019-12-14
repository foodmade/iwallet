package com.qkl.wallet.core.event;

import com.qkl.wallet.core.transfer.OrderModel;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

/**
 * @Author Jackies
 * @Date 2019/12/14 14:31
 * @Description TODO://
 **/
@Data
public class TransferEvent extends ApplicationEvent {

    private OrderModel order;

    public TransferEvent(Object source,OrderModel order) {
        super(source);
        this.order = order;
    }
}
