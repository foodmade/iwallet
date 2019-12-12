package com.qkl.wallet.vo.in;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class WithdrawParams {

    @NotNull(message = "代币币种不能为空")
    private String tokenName;

    private String chain;

    @NotNull(message = "提现订单不能为空")
    @Size(min = 1,message = "提现订单至少存在一个")
    private List<WithdrawRequest> request;
}
