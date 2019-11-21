package com.qkl.wallet.vo.out;

import com.qkl.wallet.vo.AbstractResponse;
import lombok.Data;

/**
 * @Author xiaom
 * @Date 2019/11/21 15:16
 * @Version 1.0.0
 * @Description <>
 **/
@Data
public class CreateWalletResponse {
    /**
     * 钱包地址
     */
    private String address;
    /**
     * 名称
     */
    private String walletName;
    /**
     * 秘钥
     */
    private String privateKey;

    public CreateWalletResponse(String address, String walletName, String privateKey) {
        this.address = address;
        this.walletName = walletName;
        this.privateKey = privateKey;
    }
}
