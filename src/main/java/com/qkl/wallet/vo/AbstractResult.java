package com.qkl.wallet.vo;


import com.qkl.wallet.common.enumeration.ExceptionEnum;

public abstract class AbstractResult {

    protected String code = ExceptionEnum.SUCCESS.getCode();

    protected String error;

    protected boolean success = true;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
        setCode(ExceptionEnum.SERVICEERROR.getCode());
        success = false;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
