package com.qkl.wallet.common.enumeration;

public enum CallbackTypeEnum {
    WITHDRAW_TYPE("W","提币回调"),
    RECHARGE_TYPE("D","充值回调"),
    CONFIRM_TYPE("C","区块确认");

    private String type;

    private String desc;

    CallbackTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
