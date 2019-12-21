package com.qkl.wallet.core.manage;

import com.alibaba.fastjson.JSON;
import com.qkl.wallet.common.JedisKey;
import com.qkl.wallet.common.RedisUtil;
import com.qkl.wallet.common.tools.IOCUtils;
import com.qkl.wallet.domain.OrderModel;
import com.qkl.wallet.vo.in.WithdrawParams;
import com.qkl.wallet.vo.in.WithdrawRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Author Jackies
 * @Date 2019/12/14 12:54
 * @Description TODO://
 **/
@Slf4j
public class OrderManage {

    public static void addTokenOrder(WithdrawParams params){
        String fromAddress = IOCUtils.getWalletService().foundPlatformAddress(params.getTokenName(),params.getChain());
        Assert.notNull(fromAddress,"Not found platform wallet address. TokenName:["+params.getTokenName()+"] chain:["+params.getChain()+"]");
        String contractAddress = IOCUtils.getWalletService().foundPlatformContractAddress(params.getTokenName());
        Assert.notNull(contractAddress,"Not found platform contract address. TokenName:["+params.getTokenName()+"]");
        addBatchTokenOrder(params.getRequest(),params.getTokenName(),fromAddress,contractAddress);
    }

    public static void addChainOrder(WithdrawParams params){
        String fromAddress = IOCUtils.getWalletService().foundPlatformAddress(params.getTokenName(),params.getChain());
        Assert.notNull(fromAddress,"Not found platform wallet address. TokenName:["+params.getChain()+"] chain:["+params.getChain()+"]");
        addBatchChainOrder(params.getRequest(),params.getChain(),fromAddress);
    }


    /**
     * 缓存提现订单(代币订单)
     * @param withdraw   订单基本信息
     * @param tokenName  代币名称
     */
    private static void addTokenOrder(WithdrawRequest withdraw, String tokenName,String fromAddress,String contractAddress){
        log.info("Add a new [token] withdraw order. TokenName:[{}] orderInfo:[{}]",tokenName,JSON.toJSONString(withdraw));
        IOCUtils._Get_Redis().lpushOne(JedisKey.buildTokenOrderKey(tokenName),JSON.toJSONString(new OrderModel(withdraw,tokenName,fromAddress,contractAddress)));
        log.info("Add [token] withdraw successful.");
    }

    /**
     * 缓存提现订单(批量)(代币订单)
     * @param withdraws  订单集合
     * @param tokenName  代币名称
     * @param fromAddress  平台钱包地址 (这个在发起离线交易时,需要用它获取nonce值)
     */
    private static void addBatchTokenOrder(List<WithdrawRequest> withdraws, String tokenName,String fromAddress,String contractAddress ){
        //添加到提现订单队列
        withdraws.forEach(withdraw -> addTokenOrder(withdraw,tokenName,fromAddress,contractAddress));
    }

    /**
     * 缓存提现订单(主币订单)
     * @param withdraw   订单基本信息
     * @param tokenName  主币名称
     */
    public static void addChainOrder(WithdrawRequest withdraw, String tokenName,String fromAddress){
        log.info("Add a new [chain] withdraw order. TokenName:[{}] orderInfo:[{}]",tokenName,JSON.toJSONString(withdraw));
        IOCUtils._Get_Redis().lpushOne(JedisKey.buildTokenOrderKey(tokenName),JSON.toJSONString(new OrderModel(withdraw,tokenName,fromAddress,null)));
        log.info("add [chain] withdraw successful.");
    }

    /**
     * 缓存提现订单(批量)(主币订单)
     * @param withdraws  订单集合
     * @param tokenName  主币名称
     */
    public static void addBatchChainOrder(List<WithdrawRequest> withdraws, String tokenName,String fromAddress){
        withdraws.forEach(item -> addChainOrder(item,tokenName,fromAddress));
    }

    /**
     * 从队列中弹出一个代币类型的订单
     * @param token  代币名称
     * @param cls    需要转换的实体类型
     * @param <T>    类型定义
     */
    public static <T> T lpopOrderForEntity(RedisUtil redisUtil,String token, Class<T> cls){
        Object val = redisUtil.lpopOne(JedisKey.buildTokenOrderKey(token));
        if(val == null){
            return null;
        }
        return JSON.parseObject(val.toString(),cls);
    }

    /**
     * 缓存提现订单的离线交易txHash
     * @param txnHash 区块链交易号
     * @param trace 业务服务器的跟踪ID
     */
    public static void addWithdrawTxHashNumber(String txnHash,String trace){
        if(StringUtils.isEmpty(txnHash)){
            return;
        }
        IOCUtils._Get_Redis().hset(JedisKey.buildWithdrawTxHashKey(),txnHash,trace);
    }

    /**
     * 判断txHash是否存在于离线交易txHash订单队列
     */
    public static boolean isWithdrawExist(String txHash){
        return IOCUtils._Get_Redis().hHasKey(JedisKey.buildWithdrawTxHashKey(),txHash);
    }

    /**
     * 根据txHash获取traceId
     */
    public static String getTraceId(String txHash){
        return IOCUtils._Get_Redis().hget(JedisKey.buildWithdrawTxHashKey(),txHash) + "";
    }
}
