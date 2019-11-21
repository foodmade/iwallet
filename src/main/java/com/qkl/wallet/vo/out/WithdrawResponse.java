package com.qkl.wallet.vo.out;

import com.qkl.wallet.vo.AbstractResult;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author xiaom
 * @Date 2019/11/20 17:08
 * @Version 1.0.0
 * @Description <>
 **/
@Data
public class WithdrawResponse {

    /**
     * 引擎生成的请求ID
     */
    private String requestId;
    /**
     * 提款用的热钱包地址。如果此地址中没有足够的硬币，则提款请求将等到该
     * 地址的余额足以满足请求再自动发币。
     */
    private String address;
    /**
     * 提款请求中提取的总金额。
     */
    private BigDecimal totalAmount;
    /**
     * 完成交易所需的估计交易费（仅在请求类型为ETH或BTC时可用）
     */
    private BigDecimal estimateGas;

    public WithdrawResponse(String address, BigDecimal totalAmount) {
        this.address = address;
        this.totalAmount = totalAmount;
    }

    public WithdrawResponse(String address){
        this.address = address;
    }
}
