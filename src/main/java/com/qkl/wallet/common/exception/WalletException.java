package com.qkl.wallet.common.exception;

import com.qkl.wallet.common.enumeration.ExceptionEnum;

/**
 * @Author xiaom
 * @Date 2019/12/10 18:28
 * @Version 1.0.0
 * @Description <>
 **/
public abstract class WalletException extends RuntimeException {

    private ExceptionEnum exceptionEnum = ExceptionEnum.SERVERERROR;

    private Integer code = exceptionEnum.getCode();

    private String message = exceptionEnum.getMessage();

    public WalletException(String message) {
        this.message = message;
    }

    public WalletException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public WalletException(String message, Throwable cause) {
        super(message, cause);
    }

    public WalletException() {
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
