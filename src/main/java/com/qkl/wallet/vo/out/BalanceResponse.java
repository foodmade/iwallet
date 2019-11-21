package com.qkl.wallet.vo.out;

import lombok.Data;
import java.math.BigInteger;

/**
 * @Author xiaom
 * @Date 2019/11/21 16:46
 * @Version 1.0.0
 * @Description <>
 **/
@Data
public class BalanceResponse {

    private BigInteger balance;

    public BalanceResponse(BigInteger balance) {
        this.balance = balance;
    }
}
