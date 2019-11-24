package com.qkl.wallet.common.enumeration;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

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

    /**
     * Find status enum by type.
     * @param type Enum type.
     * @return Status.
     */
    public static Optional<Status> find(Integer type){
        return Arrays.stream(Status.values()).filter(t -> t.getType().equals(type)).findFirst();
    }
}
