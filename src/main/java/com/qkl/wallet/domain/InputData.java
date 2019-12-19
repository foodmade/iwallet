package com.qkl.wallet.domain;

import lombok.Data;

import java.math.BigInteger;

/**
 * @Author Jackies
 * @Date 2019/12/18 14:03
 * @Description TODO://
 **/
@Data
public class InputData {

    private String method;

    private BigInteger amount;

    private String address;

    public InputData(String method, BigInteger amount, String address) {
        this.method = method;
        this.amount = amount;
        this.address = address;
    }
}
