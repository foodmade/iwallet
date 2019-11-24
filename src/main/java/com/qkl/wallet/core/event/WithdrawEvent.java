package com.qkl.wallet.core.event;

import com.qkl.wallet.common.enumeration.ExceptionEnum;
import com.qkl.wallet.vo.out.WithdrawCallback;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

@Data
public class WithdrawEvent extends ApplicationEvent {
    /**
     * 0 成功
     * 1 失败
     */
    private Integer type;
    /**
     * 备注
     */
    private String message;
    /**
     * 回调参数
     */
    private WithdrawCallback callbackResponse;

    public WithdrawEvent(Object source,WithdrawCallback callbackResponse,Integer type,String message) {
        super(source);
        this.type = type;
        this.message = message;
        this.callbackResponse = callbackResponse;

        if(this.type == 1){
            this.callbackResponse.setMessage(message);
            this.callbackResponse.setCode(ExceptionEnum.SERVICEERROR.getCode());
        }


    }

}
