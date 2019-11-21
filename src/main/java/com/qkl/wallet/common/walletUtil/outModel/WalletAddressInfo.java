package com.qkl.wallet.common.walletUtil.outModel;

import lombok.Data;

/**
 * @Author xiaom
 * @Date 2019/11/21 15:32
 * @Version 1.0.0
 * @Description <>
 **/
@Data
public class WalletAddressInfo {

    private String name;

    private String json;

    public WalletAddressInfo(String name, String json) {
        this.name = name;
        this.json = json;
    }
}
