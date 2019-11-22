package com.qkl.wallet.vo.in;

import lombok.Data;

import java.util.List;

@Data
public class WithdrawParams {

    private String type;

    private List<WithdrawRequest> request;
}
