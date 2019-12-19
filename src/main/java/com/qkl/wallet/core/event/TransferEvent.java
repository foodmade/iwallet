package com.qkl.wallet.core.event;

import com.qkl.wallet.domain.OrderModel;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

/**
 * @Author Jackies
 * @Date 2019/12/15 17:57
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
