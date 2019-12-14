package com.qkl.wallet.domain;

import lombok.Data;

/**
 * @Author Jackies
 * @Date 2019/12/14 21:26
 * @Description TODO://
 **/
@Data
public class ConfirmListenerEntity {

    private String transactionHash;

    private Long confirmNumber;

}
