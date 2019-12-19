package com.qkl.wallet.config;

import lombok.Data;

/**
 * @Author Jackies
 * @Date 2019/12/18 10:46
 * @Description TODO://
 **/
@Data
public class Config {

    //延迟区块高度
    private Long delayBlockNumber;

    //每次同步区块的大小
    private Integer BlockInterval;

    //最大确认区块数
    private Integer confirmMaxCount;
}
