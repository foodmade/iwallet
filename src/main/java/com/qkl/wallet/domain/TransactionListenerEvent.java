package com.qkl.wallet.domain;

import lombok.Data;

import java.util.List;

/**
 * @Author Jackies
 * @Date 2019/12/14 19:34
 * @Description TODO://合约事件(Transfer)监听器返回的数据bean
 **/
@Data
public class TransactionListenerEvent {
    private String blockHash;
    private Long logIndex;
    private String address;
    private String transactionLogIndex;
    private String signature;
    private RawBean raw;
    private Long transactionIndex;
    private String type;
    private String transactionHash;
    private ReturnValuesBean returnValues;
    private boolean removed;
    private Long blockNumber;
    private String id;
    private String event;
    private String tokenName;

    @Data
    public static class ReturnValuesBean{
        private String from;
        private String to;
        private String value;
    }

    @Data
    public static class RawBean {

        private String data;
        private List<String> topics;
    }
}
