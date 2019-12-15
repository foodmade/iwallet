package com.qkl.wallet.common.enumeration;

import com.qkl.wallet.core.event.TokenTransferEvent;
import com.qkl.wallet.core.event.TransferEvent;

import java.util.Arrays;
import java.util.Optional;

public enum TokenEventEnum {
    OWC("OWC", TokenTransferEvent.class),
    EBC("EBC",TokenTransferEvent.class),
    USDT("USDT(ERC20)", TokenTransferEvent.class),
    ETH("ETH", TransferEvent.class),
    WVP("WVP", TokenTransferEvent.class);

    private String tokenName;

    private Class<?> aClass;

    TokenEventEnum(String tokenName, Class<?> aClass) {
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
    public static Optional<TokenEventEnum> find(String type){
        return Arrays.stream(TokenEventEnum.values()).filter(t -> t.getTokenName().equals(type)).findFirst();
    }
}
