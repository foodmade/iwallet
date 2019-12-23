package com.qkl.wallet.domain;

import com.qkl.wallet.common.tools.IOCUtils;
import com.qkl.wallet.vo.in.WithdrawRequest;
import lombok.Data;
import org.apache.http.client.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Jackies
 * @Date 2019/12/14 13:08
 * @Description TODO://
 **/
@Data
public class OrderModel implements Serializable {

    private WithdrawRequest withdraw;
    private String tokenName;

    private Integer maxRetry = 10;

    protected String time;

    protected int retry;
    //平台钱包地址
    protected String fromAddress;
    //合约地址
    protected String contractAddress;
    //代币单位
    protected Long decimals;
    //交易类型
    protected String txnType;

    public OrderModel(WithdrawRequest withdraw,String tokenName,String fromAddress,String contractAddress,String txnType) {
        this.withdraw = withdraw;
        this.tokenName = tokenName;
        this.fromAddress = fromAddress;
        this.contractAddress = contractAddress;
        this.time = DateUtils.formatDate(new Date());
        this.retry = 0;
        this.decimals = IOCUtils.getWalletService().foundDecimalsByTokenName(tokenName);
        this.txnType = txnType;
    }
}
