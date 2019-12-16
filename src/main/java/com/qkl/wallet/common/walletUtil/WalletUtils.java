package com.qkl.wallet.common.walletUtil;

import com.alibaba.fastjson.JSON;
import com.qkl.wallet.common.Const;
import com.qkl.wallet.common.JedisKey;
import com.qkl.wallet.common.SpringContext;
import com.qkl.wallet.common.tools.IOCUtils;
import com.qkl.wallet.core.ContractMapper;
import com.qkl.wallet.domain.RawTransactionResEntity;
import com.qkl.wallet.service.impl.EventService;
import com.qkl.wallet.vo.in.WithdrawRequest;
import com.qkl.wallet.vo.out.CreateWalletResponse;
import lombok.extern.slf4j.Slf4j;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @Author Jackies
 * @Date 2019/12/14 17:02
 * @Description TODO://
 **/
@Slf4j
public class WalletUtils {

    /**
     * 发送离线交易
     * @param contractAddress   合约地址
     * @param toAddress         收款地址
     * @param amount            转账金额
     * @param fromAddress       打款地址
     * @return 当前交易的nonce索引
     */
    public static RawTransactionResEntity offlineTransferToken(String contractAddress, String toAddress, BigInteger amount, String fromAddress) throws ExecutionException, InterruptedException, IOException {
        BigInteger GAS_PRICE = Contract.GAS_PRICE;
        BigInteger GAS_LIMIT = Contract.GAS_LIMIT;

        Function function = new Function(
                "transfer",
                Arrays.asList(new Address(toAddress), new Uint256(amount)),
                Collections.emptyList());

        String encodedFunction = FunctionEncoder.encode(function);
        BigInteger nonce = LightWallet.getNonce(fromAddress);

        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce,
                GAS_PRICE,
                GAS_LIMIT,
                contractAddress, encodedFunction);
        //签名Transaction，这里要对交易做签名
        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, LightWallet.buildDefaultCredentials());
        String hexValue = Numeric.toHexString(signMessage);
        //发送离线交易
        EthSendTransaction ethSendTransaction;
        try {
            ethSendTransaction = SpringContext.getBean(Web3j.class).ethSendRawTransaction(hexValue).send();
        } catch (IOException e) {
            log.error("Offline transaction submission failed. ");
            throw e;
        }
        log.info("SendTransaction token order successful. Response:[{}]", JSON.toJSONString(ethSendTransaction));
        return new RawTransactionResEntity(nonce,ethSendTransaction.getTransactionHash());
    }

    /**
     * 同步交易
     */
    public static void syncTransferToken(String tokenName,String toAddress, BigInteger amount,String trace){
        log.info("Start submitting a transfer request.");
        try {
            TransactionReceipt transactionReceipt = ContractMapper.get(tokenName)
                    .transfer(toAddress ,amount)
                    .send();
            log.info("Transfer order commit successful. TokenName:[{}] toAddress:[{}] amount:[{}]",tokenName,toAddress,amount);
            log.info("Transfer receipt info:[{}]",JSON.toJSONString(transactionReceipt));
            SpringContext.getBean(EventService.class).addSuccessEvent(transactionReceipt, new WithdrawRequest(toAddress,new BigDecimal(amount),trace));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void monitorNonceIsUpdate(BigInteger nonce, String fromAddress) {
        while (true){
            try {
                BigInteger uNonce = LightWallet.getNonce(fromAddress);
                if(!nonce.equals(uNonce)){
                    break;
                }
                log.debug("Current nonce:[{}] Chain block nonce:[{}]",nonce,uNonce);
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 缓存创建的钱包信息
     * @param walletInfo 钱包对象
     */
    public static void saveWalletInfo(CreateWalletResponse walletInfo) {
        IOCUtils._Get_Redis().hset(JedisKey.buildWalletAddressKey(),walletInfo.getAddress(),JSON.toJSONString(walletInfo));
    }

    /**
     * 检查钱包地址是否属于当前钱包服务创建
     * @param walletAddress ETH钱包地址
     */
    public static boolean validWalletAddress(String walletAddress){
        if(walletAddress == null){
            return false;
        }
        return IOCUtils._Get_Redis().hHasKey(JedisKey.buildWalletAddressKey(),walletAddress);
    }

    /**
     * 单位换算 bigInt换算为eth
     */
    public static BigDecimal unitCover(BigInteger amount){
        return unitCover(new BigDecimal(amount));
    }

    public static BigDecimal unitCover(BigDecimal amount){
        return amount.divide(new BigDecimal(Const._TOKEN_UNIT),18,BigDecimal.ROUND_DOWN);
    }
}
