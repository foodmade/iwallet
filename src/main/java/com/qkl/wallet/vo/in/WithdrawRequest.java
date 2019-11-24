package com.qkl.wallet.vo.in;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author xiaom
 * @Date 2019/11/21 16:04
 * @Version 1.0.0
 * @Description <>
 **/
@Data
public class WithdrawRequest implements Serializable {

    /**
     * 转账地址
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
}
