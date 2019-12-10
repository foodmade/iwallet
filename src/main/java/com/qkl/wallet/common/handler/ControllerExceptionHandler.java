package com.qkl.wallet.common.handler;

import com.qkl.wallet.common.enumeration.ExceptionEnum;
import com.qkl.wallet.common.exception.BadRequestException;
import com.qkl.wallet.common.exception.WalletException;
import com.qkl.wallet.vo.ResultBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * All controller router's exception handler
 * @author chen
 */
@RestControllerAdvice({"com.qkl.wallet.controller"})
@Slf4j
public class ControllerExceptionHandler {

    /**
     * Global exception interceptor. Write to response. Just for OWCException .
     * @param e Exception example
     * @return error data
     */
    @ExceptionHandler({WalletException.class})
    public ResponseEntity<ResultBean<Object>> handleHaloException(WalletException e) {
        ResultBean<Object> baseResponse = handleBaseException(e);
        baseResponse.setCode(e.getCode());
        baseResponse.setData("");
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ResultBean<Object>> handlerBadException(BadRequestException ex){
        ResultBean<Object> baseResponse = handleBaseException(ex);
        baseResponse.setCode(ExceptionEnum.BAD_REQUEST_ERR.getCode());
        baseResponse.setData("");
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ResultBean<Object>> MethodArgumentNotValidHandler(MethodArgumentNotValidException e){
        ResultBean<Object> baseResponse = new ResultBean<>();
        baseResponse.setMessage(e.getBindingResult().getFieldErrors().get(0).getDefaultMessage());
        baseResponse.setCode(ExceptionEnum.PARAMS_MISS_ERR.getCode());
        baseResponse.setData("");
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    private <T> ResultBean<T> handleBaseException(Throwable t) {
        Assert.notNull(t, "Throwable must not be null");

        log.error("Captured an exception", t);

        ResultBean<T> baseResponse = new ResultBean<>();
        baseResponse.setMessage(t.getMessage());
        return baseResponse;
    }
}
