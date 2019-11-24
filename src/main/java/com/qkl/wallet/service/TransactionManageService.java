package com.qkl.wallet.service;

import com.qkl.wallet.vo.in.WithdrawRequest;
import org.springframework.lang.NonNull;

/**
 * @Author xiaom
 * @Date 2019/11/22 10:42
 * @Version 1.0.0
 * @Description <>
 **/
public interface TransactionManageService {

    /**
     * Cache transaction order to redis.
     * @param withdrawRequest Transaction basis info .
     */
    void cacheTransactionOrder(@NonNull WithdrawRequest withdrawRequest);

    /**
     * Clear transaction cache by wallet address.
     * @param address Wallet address.
     */
    void clearTransactionOrderCache(@NonNull String address);

    /**
     * Fetch transaction order info in the redis cache.
     * @param address Wallet address.
     * @return Transaction basis model.
     */
    WithdrawRequest fetchTransactionOrder(@NonNull String address);

}
