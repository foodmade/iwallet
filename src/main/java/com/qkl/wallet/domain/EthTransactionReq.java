package com.qkl.wallet.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author Jackies
 * @Date 2019/12/15 17:02
 * @Description TODO://
 **/
@NoArgsConstructor
@Data
public class EthTransactionReq {

    private String blockHash;
    private String raw;
    private Long transactionIndex;
    private String publicKey;
    private Long nonce;
    private String input;
    private String r;
    private String s;
    private String chainId;
    private String v;
    private Long blockNumber;
    private String gas;
    private String from;
    private String to;
    private BigDecimal value;
    private String hash;
    private BigDecimal gasPrice;
    private String standardV;
    private String tokenName;
}
