package com.qkl.wallet.vo.in;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @Author xiaom
 * @Date 2019/12/17 13:50
 * @Version 1.0.0
 * @Description <>
 **/
@Data
public class EthTransferParams {

    @NotNull(message = "收款地址不能为空")
    private String toAddress;

    @NotNull(message = "交易金额不能为空")
    private BigDecimal amount;

    private String fromAddress;
}
