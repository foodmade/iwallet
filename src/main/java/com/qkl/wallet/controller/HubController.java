package com.qkl.wallet.controller;

import com.qkl.wallet.domain.ConfirmListenerEntity;
import com.qkl.wallet.domain.EthTransactionReq;
import com.qkl.wallet.domain.TransactionListenerEvent;
import com.qkl.wallet.service.HubService;
import com.qkl.wallet.vo.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Jackies
 * @Date 2019/12/14 17:44
 * @Description TODO://
 **/
@RestController
@RequestMapping(value = "/hub")
public class HubController {

    @Autowired
    private HubService hubService;

    /**
     * 监听合约交易事件
     */
    @PostMapping(value = "/submitTransferEvent")
    public ResultBean<Boolean> submitTransferEvent(@RequestBody TransactionListenerEvent transactionListenerEvent){
        return ResultBean.success(hubService.submitTransferEvent(transactionListenerEvent));
    }

    /**
     * 监听确认区块数事件
     */
    @PostMapping(value = "confirmBlockNumEvent")
    public ResultBean<Boolean> confirmBlockNumEvent(@RequestBody ConfirmListenerEntity confirmListenerEntity){
        return ResultBean.success(hubService.confirmBlockNumEvent(confirmListenerEntity));
    }

    /**
     * 监听ETH交易事件
     */
    @PostMapping(value = "/ethSubmitTransferEvent")
    public ResultBean<Boolean> ethSubmitTransferEvent(@RequestBody EthTransactionReq ethTransactionReq){
        return ResultBean.success(hubService.ethSubmitTransferEvent(ethTransactionReq));
    }

}
