package com.qkl.wallet.domain;

import lombok.Data;

/**
 * @Author Jackies
 * @Date 2019/12/18 16:37
 * @Description TODO://
 **/
@Data
public class Confirm {
    /**
     * 交易hash
     */
    private String hash;
    /**
     * 所属块编号
     */
    private Long blockNumber;
    /**
     * 已确认块个数
     */
    private Long confirmBlockCnt;
    /**
     * 订单交易状态 true成功 false失败
     */
    private Boolean status;

    public Confirm(String hash, Long blockNumber,Boolean status) {
        this.hash = hash;
        this.blockNumber = blockNumber;
        this.confirmBlockCnt = 0L;
        this.status = status;
    }
}
