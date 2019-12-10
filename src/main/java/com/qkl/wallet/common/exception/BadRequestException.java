package com.qkl.wallet.common.exception;

import com.qkl.wallet.common.enumeration.ExceptionEnum;

/**
 * @Author xiaom
 * @Date 2019/12/2 17:51
 * @Version 1.0.0
 * @Description <Exception caused by bad request.>
 **/
public class BadRequestException extends WalletException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(ExceptionEnum exceptionEnum){
        super(exceptionEnum.getMessage());
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

}
