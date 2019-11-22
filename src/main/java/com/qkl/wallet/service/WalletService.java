package com.qkl.wallet.service;

import com.qkl.wallet.vo.ResultBean;
import com.qkl.wallet.vo.in.WithdrawRequest;
import com.qkl.wallet.vo.out.BalanceResponse;
import com.qkl.wallet.vo.out.CreateWalletResponse;
import com.qkl.wallet.vo.out.WithdrawResponse;

import java.util.List;

public interface WalletService {

    /**
     * 创建一个新钱包
     * @return {@link com.qkl.wallet.vo.out.CreateWalletResponse}
     */
    CreateWalletResponse createWallet();

    /**
     * 转账
     * @param withdrawRequest 转账参数
     * @return {@link com.qkl.wallet.vo.out.WithdrawResponse}
     */
    WithdrawResponse withdraw(List<WithdrawRequest> withdrawRequest);

    /**
     * 获取代币余额
     * @param address 钱包地址
     * @return {@link com.qkl.wallet.vo.out.BalanceResponse}
     */
    ResultBean<BalanceResponse> getBalance(String address);
}
