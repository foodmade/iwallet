package com.qkl.wallet.service;

import com.qkl.wallet.vo.ResultBean;
import com.qkl.wallet.vo.in.WithdrawParams;
import com.qkl.wallet.vo.in.WithdrawRequest;
import com.qkl.wallet.vo.out.BalanceResponse;
import com.qkl.wallet.vo.out.CreateWalletResponse;
import com.qkl.wallet.vo.out.GasResponse;
import com.qkl.wallet.vo.out.WithdrawResponse;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public interface WalletService {

    /**
     * 创建一个新钱包
     * @return {@link com.qkl.wallet.vo.out.CreateWalletResponse}
     */
    CreateWalletResponse createWallet();

    /**
     * 转账
     * @param params 转账参数
     * @return {@link com.qkl.wallet.vo.out.WithdrawResponse}
     */
    WithdrawResponse withdraw(WithdrawParams params) throws IOException;

    /**
     * 获取代币余额
     * @param address 钱包地址
     * @return {@link com.qkl.wallet.vo.out.BalanceResponse}
     */
    BalanceResponse getTokenBalance(@NonNull String address,@NonNull String tokenType);

    /**
     * 获取ETH钱包余额
     * @param address 钱包地址
     * @return {@link com.qkl.wallet.vo.out.BalanceResponse}
     */
    @NonNull
    BalanceResponse getETHBalance(@NonNull String address);

    /**
     * 默认使用服务器默认钱包账户转账
     * 以太币转账
     * @param toAddress  收款地址
     * @param amount     金额
     * @return 结果
     */
    Boolean transferEth(String toAddress, BigDecimal amount);

    /**
     * 获取最新一笔交易的gas费用
     */
    GasResponse getEthGas();

    /**
     * 获取对应币种下的平台钱包余额
     */
    BalanceResponse getPlatformBalance(@NonNull String chain,@NonNull String tokenName);

    /**
     * 通过代币名称获取对应的平台钱包地址
     * @param tokenName 代币名称
     * @param chain 链类型 ETH/BTC
     * @return 钱包地址
     */
    String parserPlatformAddress(String chain,String tokenName);

    /**
     * 通过代币名称获取对应的平台钱包地址
     * @param tokenName  代币名称
     * @return 钱包地址
     */
    String parserPlatformAddress(String tokenName);

}
