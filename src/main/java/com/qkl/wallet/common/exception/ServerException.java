package com.qkl.wallet.common.exception;

import com.qkl.wallet.common.enumeration.ExceptionEnum;

/**
 * @Author Jackies
 * @Date 2019/12/14 11:17
 * @Description TODO://
 **/
public class ServerException extends WalletException{

    public ServerException(String message) {
        super(message);
    }

    public ServerException(ExceptionEnum exceptionEnum){
        super(exceptionEnum.getMessage());
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

}
