package com.qkl.wallet.common.exception;

import com.qkl.wallet.common.enumeration.ExceptionEnum;

/**
 * @Author xiaom
 * @Date 2019/11/21 16:32
 * @Version 1.0.0
 * @Description <>
 **/
public class InvalidException extends RuntimeException {

    private static final long serialVersionUID = -6642853129794871992L;

    private Integer code;

    public InvalidException() {
        this.code = ExceptionEnum.SERVICEERROR.getCode();
    }

    public InvalidException(String message) {
        super(message);
        this.code = ExceptionEnum.SERVICEERROR.getCode();
    }
}
