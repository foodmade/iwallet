package com.qkl.wallet.common.enumeration;

public enum ExceptionEnum {

    SUCCESS("10000", "成功"),
    SERVERERROR("10001", "服务器异常，请联系管理员"),
    SERVICEERROR("10005", "远程服务异常");

    private String code;
    private String message;

    ExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 号码
     * @return
     */
    public String getCode() {
        return code;
    }

    /**
     * 信息
     * @return
     */
    public String getMessage() {
        return message;
    }
}
