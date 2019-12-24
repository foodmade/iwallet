package com.qkl.wallet.common.enumeration;

import java.util.Arrays;
import java.util.Optional;

public enum CallbackTypeEnum {
    WITHDRAW_TYPE("W","提币回调"),
    RECHARGE_TYPE("D","充值回调"),
    CONFIRM_TYPE("C","区块确认"),
    DRAW_IN("IN","划入"),
    DRAW_OUT("OUT","划出"),
    DRAW_TYPE("DRAW","划转");

    private String type;

    private String desc;

    CallbackTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static Optional<CallbackTypeEnum> find(String type){
        return Arrays.stream(CallbackTypeEnum.values()).filter(t -> t.getType().equals(type)).findFirst();
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
