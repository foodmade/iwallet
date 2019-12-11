package com.qkl.wallet.common.enumeration;

import com.qkl.wallet.contract.Token;
import com.qkl.wallet.contract.Usdt;

import java.util.Arrays;
import java.util.Optional;

public enum TokenTypeEnum {
    OWC("OWC", Token.class),
    EBC("EBC",Token.class),
    USDT("USDT", Usdt.class);

    private String tokenName;

    private Class<?> aClass;

    TokenTypeEnum(String tokenName, Class<?> aClass) {
        this.tokenName = tokenName;
        this.aClass = aClass;
    }

    public String getTokenName() {
        return tokenName;
    }

    public Class<?> getaClass() {
        return aClass;
    }

    /**
     * Find status enum by type.
     * @param type Enum type.
     * @return Status.
     */
    public static Optional<TokenTypeEnum> find(String type){
        return Arrays.stream(TokenTypeEnum.values()).filter(t -> t.getTokenName().equals(type)).findFirst();
    }
}
