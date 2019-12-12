package com.qkl.wallet.common.enumeration;

import java.util.Arrays;
import java.util.Optional;

public enum ChainEnum {
    ETH("ETH","以太坊",true),
    BTC("BTC","比特币",false);
    private String chainName;

    private String desc;

    private boolean valid;

    ChainEnum(String chainName, String desc,boolean valid) {
        this.chainName = chainName;
        this.desc = desc;
        this.valid = valid;
    }

    public String getChainName() {
        return chainName;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isValid() {
        return valid;
    }

    public static Optional<ChainEnum> find(String chain){
        return Arrays.stream(ChainEnum.values()).filter(t -> t.getChainName().equals(chain)).findFirst();
    }
}
