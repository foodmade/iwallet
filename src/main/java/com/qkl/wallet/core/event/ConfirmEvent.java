package com.qkl.wallet.core.event;

import com.qkl.wallet.domain.ConfirmListenerEntity;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

/**
 * @Author Jackies
 * @Date 2019/12/14 21:39
 * @Description TODO://
 **/
@Data
public class ConfirmEvent extends ApplicationEvent {

    private ConfirmListenerEntity confirmListenerEntity;

    public ConfirmEvent(Object source,ConfirmListenerEntity confirmListenerEntity) {
        super(source);
        this.confirmListenerEntity = confirmListenerEntity;
    }
}
