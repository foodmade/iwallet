package com.qkl.wallet.vo.in;

import com.qkl.wallet.vo.AbstractResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author xiaom
 * @Date 2019/11/20 17:26
 * @Version 1.0.0
 * @Description <>
 **/
@Data
public class WithdrawRequest {

    private List<WithdrawBaseInfo> datas = new ArrayList<>();

    private String type;
}
@EqualsAndHashCode(callSuper = true)
@Data
class WithdrawBaseInfo extends AbstractResult {
    /**
     * 钱包地址
     */
    private String walletAddress;

    /**
     * 提现金额
     */
    private BigDecimal withdrawAmount;
    /**
     * 追踪ID
     */
    private String traceId;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 订单号
     */
    private Long withdrawFlowId;
    /**
     * 手续费
     */
    private BigDecimal poundageAmount;
}
