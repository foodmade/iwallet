package com.qkl.wallet.vo;

import com.qkl.wallet.common.enumeration.ExceptionEnum;
import com.qkl.wallet.vo.out.WithdrawResponse;
import jnr.ffi.annotations.In;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author xiaom
 * @Date 2019/11/21 15:25
 * @Version 1.0.0
 * @Description <>
 **/
public class ResultBean<T> implements Serializable {
    /**
     * 是否业务正确处理。
     */
    private boolean success;

    /**
     * 业务处理消息。
     */
    private String message;

    /**
     *
     */
    private Integer code;

    /**
     * 时间
     */
    private Date resDate = new Date();

    /**
     * 返回的数据。
     */
    private T data;

    public ResultBean setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ResultBean setMessage(String message) {
        this.message = message;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public ResultBean setCode(Integer code) {
        this.code = code;
        return this;
    }

    public T getData() {
        return data;
    }

    public ResultBean setData(T data) {
        this.data = data;
        return this;
    }

    public static <T> ResultBean<T> result(){
        return new ResultBean().setCode(1).setSuccess(true).setMessage("操作成功").setData(null);
    }


    public static <T> ResultBean<T> result(int code ,String message){
        return new ResultBean().setCode(code).setSuccess(1==code?true:false).setMessage(message).setData(null);
    }

    public static <T> ResultBean<T> result(int code ,String message,T data){
        return new ResultBean().setCode(code).setSuccess(1==code?true:false).setMessage(message).setData(data);
    }

    public static <T> ResultBean<T> success(T data){
        return new ResultBean().setCode(ExceptionEnum.SUCCESS.getCode()).setSuccess(true).setMessage("操作成功").setData(data);
    }

    public static <T> ResultBean<T> exception(String message) {
        return new ResultBean().setCode(ExceptionEnum.SERVERERROR.getCode()).setSuccess(false).setMessage(message).setData(null);
    }

    public Date getResDate() {
        return resDate;
    }

    public void setResDate(Date resDate) {
        this.resDate = resDate;
    }
}
