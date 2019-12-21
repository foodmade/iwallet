package com.qkl.wallet.core.event.listener;

import com.alibaba.fastjson.JSON;
import com.qkl.wallet.common.Const;
import com.qkl.wallet.common.JedisKey;
import com.qkl.wallet.common.RedisUtil;
import com.qkl.wallet.common.walletUtil.WalletUtils;
import com.qkl.wallet.core.event.MonitorTransactionEvent;
import com.qkl.wallet.core.manage.OrderManage;
import com.qkl.wallet.domain.Confirm;
import com.qkl.wallet.domain.InputData;
import com.qkl.wallet.service.WalletService;
import com.qkl.wallet.service.impl.EventService;
import com.qkl.wallet.vo.out.WithdrawCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @Author Jackies
 * @Date 2019/12/18 12:17
 * @Description TODO://
 **/
@Component
@Slf4j
public class MonitorTransactionEventListener {

    @Autowired
    private Web3j web3j;
    @Autowired
    private EventService eventService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private RedisUtil redisUtil;

    @EventListener
    public void onApplicationEvent(MonitorTransactionEvent event) {

        Long startBlockNumber = event.getStartBlockNumber();
        Long endBlockNumber = event.getEndBlockNumber();

        web3j.replayPastBlocksFlowable(new DefaultBlockParameterNumber(startBlockNumber),
                new DefaultBlockParameterNumber(endBlockNumber),true,true).subscribe(block -> {
            Optional.ofNullable(block)
                    .map(ethBlock -> block.getBlock())
                    .filter(result -> !result.getTransactions().isEmpty())
                    .ifPresent(transaction -> transactionHandler(transaction.getTransactions()));
        }).isDisposed();
    }

    /**
     * 循环每笔订单,解析出toAddress,然后判断当前地址是不是此钱包服务创建,如果不是则跳过,是则进行回调
     * ps: 由于监听到是eth的所有交易
     */
    private void transactionHandler(List<TransactionResult> transactionResultList){
        log.debug("Handler Eth Transaction list.");
        for (TransactionResult transactionResult : transactionResultList) {
            EthBlock.TransactionObject transactionObject = (EthBlock.TransactionObject)transactionResult;

            if(transactionObject == null){
                continue;
            }

            log.debug("Chain Block Number:[{}]",transactionObject.getBlockNumber());
            log.debug("TransferObj:{}",JSON.toJSONString(transactionObject));

            String input = transactionObject.getInput();

            if(Const._EMPTY_HEX.equals(input)){
                //如果input是0x(空输入),则认为是ETH交易,分发给ETH处理器
                ethMonitorHandler(transactionObject);
            }else{
                tokenMonitorHandler(transactionObject);
            }
        }
    }

    /**
     * 1 将hash加入到任务队列
     * 2 将confirm详细信息保存至redis
     * 区块确认器会从任务队列中获取hash进行对比,如果已经达到最大确认数,则会将对应hash从任务队列移除
     */
    private void createConfirmBlockNumberTask(Confirm confirm) {
        //保存基本信息
        redisUtil.hset(JedisKey._CONFIRM_HASH_INFO,confirm.getHash(),JSON.toJSONString(confirm));
        if(confirm.getStatus()){
            //添加到任务队列
            redisUtil.sSet(JedisKey._CONFIRM_SCAN_QUEUE,confirm.getHash());
        }
    }

    private void tokenMonitorHandler(EthBlock.TransactionObject transactionObject) {

        InputData inputData;
        //解析input数据
        try {
            inputData = WalletUtils.decodeInput(transactionObject.getInput());
            if(!WalletUtils.validWalletAddress(inputData.getAddress())){
                return;
            }

            //交易状态
            boolean status = walletService.validTransferStatus(transactionObject.getHash());

            //解析合约地址,获取tokenName
            String contractAddress = transactionObject.getTo();
            String tokenName = walletService.foundPlatformTokenName(contractAddress);
            log.info("Token transfer valid passed. \n tokenName:[{}] \n fromAddress:[{}] \n toAddress:[{}] \n amount:[{}] \n status:[{}] \n transaction detail:{}"
                    ,tokenName,transactionObject.getFrom(),inputData.getAddress(),inputData.getAmount(),status,JSON.toJSONString(transactionObject));

            callbackServer(loadCallback(transactionObject,inputData.getAddress(),WalletUtils.unitCover(inputData.getAmount(),tokenName),tokenName,status));
            //创建确认区块数任务
            createConfirmBlockNumberTask(new Confirm(transactionObject.getHash(),transactionObject.getBlockNumber().longValue(),status));
        } catch (Exception e) {
            log.error("Token transfer handler throw error. inputStr:[{}] message:{{}}",transactionObject.getInput(),e.getMessage());
        }
    }

    private void ethMonitorHandler(EthBlock.TransactionObject transactionObject) {
        if(!WalletUtils.validWalletAddress(transactionObject.getTo())){
            return;
        }

        String eth = "ETH";

        //交易状态
        boolean status = walletService.validTransferStatus(transactionObject.getHash());

        log.info("Eth transfer valid passed. \n tokenName:[ETH] \n fromAddress:[{}] \n toAddress:[{}] \n amount:[{}] status:[{}]",transactionObject.getFrom(),
                transactionObject.getTo(),transactionObject.getValue(),status);
        callbackServer(loadCallback(transactionObject,transactionObject.getTo(),WalletUtils.unitCover(transactionObject.getValue(),eth),eth,status));
        //创建确认区块数任务
        createConfirmBlockNumberTask(new Confirm(transactionObject.getHash(),transactionObject.getBlockNumber().longValue(),status));
    }

    private WithdrawCallback loadCallback(EthBlock.TransactionObject event, String toAddress, BigDecimal amount,String tokenName,Boolean status){
        WithdrawCallback callback = new WithdrawCallback(WalletUtils.judgeOrderType(event.getHash()));
        callback.setTxnHash(event.getHash());
        callback.setAmount(amount.toPlainString());
        callback.setSender(event.getFrom());
        callback.setRecepient(toAddress);
        callback.setTrace(OrderManage.getTraceId(event.getHash()));
        callback.setTokenName(tokenName);
        callback.setStatus(status);
        callback.setGas(event.getGas().toString());
        return callback;
    }

    private void callbackServer(WithdrawCallback callback){
        eventService.addSuccessEvent(callback);
    }

}
