package com.qkl.wallet.common.enumeration;

import lombok.Getter;

@Getter
public enum Status {
    SUCCESS(0,"成功"),
    FAIL(1,"失败");

    private Integer type;

    private String desc;

    Status(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
