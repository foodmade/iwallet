package com.qkl.wallet.service;

import com.qkl.wallet.common.Const;
import com.qkl.wallet.common.enumeration.CallbackTypeEnum;
import com.qkl.wallet.common.enumeration.Status;
import com.qkl.wallet.contract.Token;
import com.qkl.wallet.service.impl.EventService;
import com.qkl.wallet.vo.in.WithdrawRequest;
import com.qkl.wallet.vo.out.WithdrawCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigDecimal;

/**
 * @Author xiaom
 * @Date 2019/11/25 14:31
 * @Version 1.0.0
 * @Description <>
 **/
@Service
@Slf4j
public class EventServiceImpl implements EventService {
    @Override
    public void addSuccessEvent(WithdrawCallback response) {
        addEventQueue(response, Status.SUCCESS,"");
    }

    @Override
    public void addErrEvent(WithdrawRequest withdrawRequest, String message) {
        addEventQueue(new WithdrawCallback(withdrawRequest.getAddress(), CallbackTypeEnum.WITHDRAW_TYPE),Status.FAIL,message);
    }

    @Override
    public void addSuccessEvent(TransactionReceipt receipt, WithdrawRequest request) {
        addSuccessEvent(assemblyTransactionCallModel(receipt,request));
    }

    @Override
    public void addSuccessEvent(Token.TransferEventResponse eventResponse) {
        addSuccessEvent(assemblyEventResponseCallModel(eventResponse));
    }

    private WithdrawCallback assemblyEventResponseCallModel(Token.TransferEventResponse eventResponse){
        WithdrawCallback callback = new WithdrawCallback(CallbackTypeEnum.RECHARGE_TYPE);
        callback.setAmount(new BigDecimal(eventResponse.value).divide(new BigDecimal(Const._UNIT),18, BigDecimal.ROUND_DOWN) + "");
        callback.setRecepient(eventResponse.to);
        callback.setTxnHash(eventResponse.log.getTransactionHash());
        callback.setSender(eventResponse.from);
        callback.setTxnHash(eventResponse.log.getTransactionHash());
        return callback;
    }

    private WithdrawCallback assemblyTransactionCallModel(TransactionReceipt receipt, WithdrawRequest request) {
        WithdrawCallback callback = new WithdrawCallback(CallbackTypeEnum.WITHDRAW_TYPE);
        callback.setAmount(request.getAmount()+"");
        callback.setGas(receipt.getGasUsed()+"");
        callback.setRecepient(receipt.getTo());
        callback.setSender(receipt.getFrom());
        callback.setTxnHash(receipt.getTransactionHash());
        callback.setTrace(request.getTrace());
        return callback;
    }
}
