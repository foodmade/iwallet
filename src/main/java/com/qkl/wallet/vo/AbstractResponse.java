package com.qkl.wallet.vo;

import com.qkl.wallet.common.enumeration.ExceptionEnum;
import lombok.Data;

/**
 * @Author xiaom
 * @Date 2019/11/20 17:16
 * @Version 1.0.0
 * @Description <>
 **/
@Data
public abstract class AbstractResponse {

    private Integer code = ExceptionEnum.SUCCESS.getCode();
    private String url;
    private boolean success;
}
