package com.qkl.wallet.vo.out;


import com.qkl.wallet.common.enumeration.CallbackTypeEnum;
import com.qkl.wallet.common.enumeration.ExceptionEnum;
import lombok.Data;

/**
 * @Author xiaom
 * @Date 2019/11/22 14:06
 * @Version 1.0.0
 * @Description <>
 **/

@Data
public class WithdrawCallback {

    /**
     * 引擎中交易的ID
     */
    private String _id;

    /**
     * 交易类型（ D - 存款）(W - 提款)
     */
    private String txnType;

    /**
     * 发送地址
     */
    private String sender;
    /**
     * 接收地址
     */
    private String recepient;
    /**
     * 金额
     */
    private String amount;
    /**
     * 交易哈希
     */
    private String txnHash;
    /**
     * 交易创建时间
     */
    private String createTime;
    /**
     * 交易费（如果coinType是 ETH 或 BTC）
     */
    private String gas = "0";
    /**
     * 跟踪ID
     */
    private String trace;
    /**
     * 消息
     */
    private String message;
    /**
     * 状态码
     */
    private Integer code = ExceptionEnum.SUCCESS.getCode();
    /**
     * 代币名称
     */
    private String tokenName;
    /**
     * 区块确认数
     */
    private Long confirmBlockNumber;
    /**
     * 交易状态
     */
    private Boolean status;

    public WithdrawCallback(String recepient, CallbackTypeEnum callbackTypeEnum) {
        this.recepient = recepient;
        this.txnType = callbackTypeEnum.getType();
    }

    public WithdrawCallback(CallbackTypeEnum callbackTypeEnum) {
        this.txnType = callbackTypeEnum.getType();
    }
}
