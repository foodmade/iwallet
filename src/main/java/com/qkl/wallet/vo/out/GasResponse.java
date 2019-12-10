package com.qkl.wallet.vo.out;

import lombok.Data;

import java.math.BigInteger;

/**
 * @Author Jackies
 * @Date 2019/12/10 21:18
 * @Description TODO://
 **/
@Data
public class GasResponse {

    private BigInteger gas;

    public GasResponse(BigInteger gas) {
        this.gas = gas;
    }
}
