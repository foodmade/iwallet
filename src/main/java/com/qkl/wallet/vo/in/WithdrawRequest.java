package com.qkl.wallet.vo.in;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author xiaom
 * @Date 2019/11/21 16:04
 * @Version 1.0.0
 * @Description <>
 **/
@Data
@EqualsAndHashCode
public class WithdrawRequest implements Serializable {

    /**
     * 打款地址
     */
    private String fromAddress;

    /**
     * 收款地址
     */
    private String address;
    /**
     * 转账金额
     */
    private BigDecimal amount;
    /**
     * 追踪ID
     */
    private String trace;

    public WithdrawRequest(String address, BigDecimal amount, String trace) {
        this.address = address;
        this.amount = amount;
        this.trace = trace;
    }

    public WithdrawRequest(String fromAddress,String toaddress, BigDecimal amount, String trace){
        this.address = address;
        this.amount = amount;
        this.trace = trace;
        this.fromAddress = fromAddress;
    }
}
