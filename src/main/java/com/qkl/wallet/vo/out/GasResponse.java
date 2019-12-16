package com.qkl.wallet.vo.out;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @Author Jackies
 * @Date 2019/12/10 21:18
 * @Description TODO://
 **/
@Data
public class GasResponse {

    private BigDecimal gas;

    public GasResponse(BigDecimal gas) {
        this.gas = gas;
    }
}
