package com.qkl.wallet.common.exception;

import com.qkl.wallet.common.enumeration.ExceptionEnum;

/**
 * @Author xiaom
 * @Date 2019/11/21 16:32
 * @Version 1.0.0
 * @Description <>
 **/
public class InvalidException extends WalletException {

    public InvalidException(String message) {
        super(message);
    }

    public InvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
