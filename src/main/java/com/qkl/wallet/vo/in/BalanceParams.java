package com.qkl.wallet.vo.in;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author Jackies
 * @Date 2019/12/12 22:27
 * @Description TODO://
 **/
@Data
public class BalanceParams {

    @NotNull(message = "Belonging chain type cannot be empty")
    private String chain;

    @NotNull(message = "Token name cannot be empty")
    private String tokenName;
}
