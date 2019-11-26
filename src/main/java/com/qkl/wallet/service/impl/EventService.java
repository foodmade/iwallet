package com.qkl.wallet.service.impl;

import com.qkl.wallet.common.SpringContext;
import com.qkl.wallet.common.enumeration.Status;
import com.qkl.wallet.contract.Token;
import com.qkl.wallet.core.event.WithdrawEvent;
import com.qkl.wallet.vo.in.WithdrawRequest;
import com.qkl.wallet.vo.out.WithdrawCallback;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

/**
 * @Author xiaom
 * @Date 2019/11/25 14:29
 * @Version 1.0.0
 * @Description <>
 **/
public interface EventService {

    void addSuccessEvent(WithdrawCallback response);

    void addSuccessEvent(TransactionReceipt receipt,WithdrawRequest request);

    void addSuccessEvent(Token.TransferEventResponse eventResponse);

    void addErrEvent(WithdrawRequest withdrawRequest, String message);

    default void addEventQueue(WithdrawCallback response, Status status, String message) {
        SpringContext.getApplicationContext().publishEvent(new WithdrawEvent(this,response,status.getType(),message));
    }
}
