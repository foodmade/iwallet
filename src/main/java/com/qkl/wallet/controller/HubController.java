package com.qkl.wallet.controller;

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

    @PostMapping(value = "/submitTransferEvent")
    public ResultBean<Boolean> submitTransferEvent(@RequestBody TransactionListenerEvent transactionListenerEvent){
        return ResultBean.success(hubService.submitTransferEvent(transactionListenerEvent));
    }

    @PostMapping(value = "confirmBlockNumEvent")
    public ResultBean<Boolean> confirmBlockNumEvent(){
        return null;
    }

}
