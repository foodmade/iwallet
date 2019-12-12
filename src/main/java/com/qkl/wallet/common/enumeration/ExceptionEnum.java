package com.qkl.wallet.common.enumeration;

public enum ExceptionEnum {

    SUCCESS(10000,"Success." ,"成功"),
    SERVERERROR(10001,"Service exception","服务器异常，请联系管理员"),
    SERVICEERROR(10005,"Service exception", "远程服务异常"),
    PARAMS_MISS_ERR(10006,"Params missing","参数缺失"),
    BAD_REQUEST_ERR(10007,"Bad request.","请求失败"),
    INVALID_TOKEN_ERR(10008,"Undefined token type","未定义的代币类型"),
    NOT_SUPPORT_ERR(10009,"This type is not currently supported","暂时不支持此代币类型");

    private Integer code;

    private String message;

    private String CNMessage;

    ExceptionEnum(Integer code, String message,String CNMessage) {
        this.code = code;
        this.message = message;
        this.CNMessage = CNMessage;
    }
    /**
     * 号码
     * @return
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 信息
     * @return
     */
    public String getMessage() {
        return message;
    }

    public String getCNMessage() {
        return CNMessage;
    }
}
