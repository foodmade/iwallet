package com.qkl.wallet.service.impl;

import com.alibaba.fastjson.JSON;
import com.qkl.wallet.common.JedisKey;
import com.qkl.wallet.common.RedisUtil;
import com.qkl.wallet.service.TransactionManageService;
import com.qkl.wallet.vo.in.WithdrawRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author xiaom
 * @Date 2019/11/22 10:42
 * @Version 1.0.0
 * @Description <>
 **/
@Service
public class TransactionManageServiceImpl implements TransactionManageService {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void cacheTransactionOrder(WithdrawRequest withdrawRequest) {
        redisUtil.set(JedisKey.buildWalletOrderKey(withdrawRequest.getAddress()), JSON.toJSONString(withdrawRequest));
    }

    @Override
    public void clearTransactionOrderCache(String address) {
        redisUtil.del(JedisKey.buildWalletOrderKey(address));
    }

    @Override
    public WithdrawRequest fetchTransactionOrder(String address) {
        return (WithdrawRequest)redisUtil.get(JedisKey.buildWalletOrderKey(address));
    }
}
