package com.qkl.wallet.service;

import com.qkl.wallet.vo.in.BalanceParams;
import com.qkl.wallet.vo.in.EthTransferParams;
import com.qkl.wallet.vo.in.WithdrawParams;
import com.qkl.wallet.vo.out.BalanceResponse;
import com.qkl.wallet.vo.out.CreateWalletResponse;
import com.qkl.wallet.vo.out.GasResponse;
import com.qkl.wallet.vo.out.WithdrawResponse;
import org.springframework.lang.NonNull;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import java.io.IOException;
import java.math.BigDecimal;

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
     * 2包之前互相转账,fromAddress必须是平台创建的钱包,会自动查找对应的秘钥
     * @param fromAddress  打款地址
     * @param toAddress    收款地址
     * @param amount       金额
     */
    Boolean transferEth(String fromAddress,String toAddress,BigDecimal amount);

    /**
     * 2个钱包间的互转
     * @param fromAddress   打款地址
     * @param toAddress     收款地址
     * @param amount        金额
     * @param secretKey     打款钱包秘钥
     */
    TransactionReceipt transferEth(String fromAddress, String toAddress, BigDecimal amount, String secretKey);

    /**
     * 以太币之间的转账,使用任务队列方式执行
     * 这个转账优先级默认最高,加入后会放到队列头
     * @param ethTransferParams 转账参数
     */
    Boolean transferEth(EthTransferParams ethTransferParams);

    /**
     * 获取最新一笔交易的gas费用
     */
    GasResponse getEthGasResponse();

    /**
     * 获取推荐燃油费
     */
    EthGasPrice getGasPrice();

    /**
     * 获取对应币种下的平台钱包余额
     */
    BalanceResponse getPlatformBalance(BalanceParams balanceParams);

    /**
     * 通过代币名称获取对应的平台钱包地址 (代币)
     * @param tokenName 代币名称
     * @param chain 链类型 ETH/BTC
     * @return 钱包地址
     */
    String foundPlatformAddress(String chain, String tokenName);

    /**
     * 通过代币名称获取对应的平台钱包地址 (主币)
     * @param tokenName  代币名称
     * @return 钱包地址
     */
    String foundPlatformAddress(String tokenName);

    /**
     * 通过代币名称获取对应的合约地址
     * @param tokenName 代币名称
     * @return 智能合约地址
     */
    String foundPlatformContractAddress(String tokenName);

    /**
     * 通过合约地址获取代币名称
     * @param contractAddress 合约地址
     * @return 代币名称
     */
    String foundPlatformTokenName(String contractAddress);

    /**
     * 通过代币名称获取对应的钱包秘钥
     */
    String foundTokenSecretKey(String chain);

    /**
     * 验证交易是否成功
     * @param hash 交易hash
     */
    boolean validTransferStatus(String hash);

    /**
     * 获取交易信息 通过交易Hash
     * @param hash 交易hash
     */
    EthTransaction fetchTransactionInfoByHash(String hash);

    /**
     * 通过币种获取对应的小数位数
     */
    Long foundDecimalsByTokenName(String tokenName);
}
