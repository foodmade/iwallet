package com.qkl.wallet.core.event.listener;

import com.qkl.wallet.common.enumeration.CallbackTypeEnum;
import com.qkl.wallet.domain.OrderModel;
import com.qkl.wallet.service.impl.EventService;
import com.qkl.wallet.vo.OrderBaseInfo;
import com.qkl.wallet.vo.out.WithdrawCallback;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author xiaom
 * @Date 2019/12/23 16:12
 * @Version 1.0.0
 * @Description <>
 **/
public abstract class Listener {

    @Autowired
    private EventService eventService;

    protected void callbackErrMessage(OrderModel orderModel, String errMessage) {
        WithdrawCallback callback = new WithdrawCallback(CallbackTypeEnum.WITHDRAW_TYPE);
        callback.setStatus(false);
        callback.setMessage(errMessage);
        callback.setTrace(orderModel.getWithdraw().getTrace());
        callback.setTokenName(orderModel.getTokenName());
        callback.setAmount(orderModel.getWithdraw().getAmount().toPlainString());
        eventService.addSuccessEvent(callback);
    }

    protected OrderBaseInfo loadBaseOrder(String trace,String txnType){
        return new OrderBaseInfo(trace,txnType);
    }

}
