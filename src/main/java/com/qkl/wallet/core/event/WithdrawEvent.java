package com.qkl.wallet.core.event;

import com.qkl.wallet.vo.in.WithdrawRequest;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

@Data
public class WithdrawEvent extends ApplicationEvent {

    private WithdrawRequest withdrawRequest;
    /**
     * 0 成功
     * 1 失败
     */
    private Integer type;
    /**
     * 备注
     */
    private String message;

    public WithdrawEvent(Object source,WithdrawRequest withdrawRequest,Integer type,String message) {
        super(source);
        this.withdrawRequest = withdrawRequest;
        this.type = type;
        this.message = message;
    }
}
