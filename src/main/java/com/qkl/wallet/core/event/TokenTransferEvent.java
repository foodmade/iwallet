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
public class TokenTransferEvent extends ApplicationEvent {

    private OrderModel order;

    public TokenTransferEvent(Object source, OrderModel order) {
        super(source);
        this.order = order;
    }
}
