package com.qkl.wallet.core.transfer;

import com.qkl.wallet.vo.in.WithdrawRequest;
import lombok.Data;
import org.apache.http.client.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Jackies
 * @Date 2019/12/14 13:08
 * @Description TODO://
 **/
@Data
public class OrderModel implements Serializable {

    private WithdrawRequest withdraw;
    private String tokenName;

    private Integer maxRetry = 10;

    protected String time;

    protected int retry;
    //平台钱包地址
    protected String fromAddress;
    //合约地址
    protected String contractAddress;

    public OrderModel(WithdrawRequest withdraw,String tokenName,String fromAddress,String contractAddress) {
        this.withdraw = withdraw;
        this.tokenName = tokenName;
        this.fromAddress = fromAddress;
        this.contractAddress = contractAddress;
        this.time = DateUtils.formatDate(new Date());
        this.retry = 0;
    }

    public static List<OrderModel> buildModels(List<WithdrawRequest> withdrawRequests,String tokenName,String fromAddress,String contractAddress){
        return withdrawRequests.parallelStream()
                .map(withdraw -> new OrderModel(withdraw,tokenName,fromAddress,contractAddress)).collect(Collectors.toList());
    }
}
