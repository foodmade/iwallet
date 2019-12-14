package com.qkl.wallet.domain;

import lombok.Data;

import java.math.BigInteger;

/**
 * @Author Jackies
 * @Date 2019/12/14 19:07
 * @Description TODO://
 **/
@Data
public class RawTransactionResEntity {

    private BigInteger nonce;

    private String transactionHash;

    public RawTransactionResEntity(BigInteger nonce, String transactionHash) {
        this.nonce = nonce;
        this.transactionHash = transactionHash;
    }
}
