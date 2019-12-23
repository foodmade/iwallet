package com.qkl.wallet.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author xiaom
 * @Date 2019/12/23 17:39
 * @Version 1.0.0
 * @Description <>
 **/
@Data
@AllArgsConstructor
public class OrderBaseInfo {

    private String traceId;

    private String txnType;

}
