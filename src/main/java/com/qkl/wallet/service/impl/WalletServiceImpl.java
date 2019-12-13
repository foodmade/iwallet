package com.qkl.wallet.service.impl;

import com.alibaba.fastjson.JSON;
import com.qkl.wallet.common.Const;
import com.qkl.wallet.common.UtilsService;
import com.qkl.wallet.common.enumeration.ChainEnum;
import com.qkl.wallet.common.enumeration.ExceptionEnum;
import com.qkl.wallet.common.exception.BadRequestException;
import com.qkl.wallet.common.walletUtil.LightWallet;
import com.qkl.wallet.common.walletUtil.outModel.WalletAddressInfo;
import com.qkl.wallet.config.ApplicationConfig;
import com.qkl.wallet.config.TokenConfigs;
import com.qkl.wallet.contract.IToken;
import com.qkl.wallet.contract.Token;
import com.qkl.wallet.core.ContractMapper;
import com.qkl.wallet.service.TransactionManageService;
import com.qkl.wallet.service.WalletService;
import com.qkl.wallet.vo.in.WithdrawParams;
import com.qkl.wallet.vo.in.WithdrawRequest;
import com.qkl.wallet.vo.out.BalanceResponse;
import com.qkl.wallet.vo.out.CreateWalletResponse;
import com.qkl.wallet.vo.out.GasResponse;
import com.qkl.wallet.vo.out.WithdrawResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @Author xiaom
 * @Date 2019/11/21 14:59
 * @Version 1.0.0
 * @Description <>
 **/
@Service
@Slf4j
public class WalletServiceImpl implements WalletService {

    @Autowired
    private Web3j web3j;
    @Autowired
    private TransactionManageService transactionManageService;
    @Autowired
    private EventService eventService;
    @Autowired
    private TokenConfigs tokenConfigs;

    @Override
    public CreateWalletResponse createWallet() {
        try {
            WalletAddressInfo walletAddressInfo = LightWallet.createNewWallet(ApplicationConfig.defaultPassword);
            Credentials credentials = LightWallet.openWallet(ApplicationConfig.defaultPassword, walletAddressInfo.getName());
            String privateKey = Numeric.toHexStringNoPrefix(credentials.getEcKeyPair().getPrivateKey());
            return new CreateWalletResponse(credentials.getAddress(),walletAddressInfo.getName(),privateKey);
        } catch (Exception e) {
            log.error("Create wallet throw error . throw info >>> [{}]",e.getMessage());
            return null;
        }
    }

    @Override
    public WithdrawResponse withdraw(WithdrawParams params) throws IOException {

        //判断是不是BTC或者ETH主链之间的交易
        if(params.getTokenName() == null){
            //如果子链参数为空,则说明这是BTC或者ETH之间的交易,分发给主链交易流程
            mainChainTransfer(params);
        }else{
            tokenTransfer(params);
        }
        return new WithdrawResponse("1111");
    }

    private void mainChainTransfer(WithdrawParams params) {
        if(UtilsService.isEth(params.getChain())){

        }
    }

    private void tokenTransfer(WithdrawParams params) {
        //Load contract client.
        IToken myToken;
        try {
            myToken = ContractMapper.get(params.getTokenName());
        } catch (Exception e) {
            throw new BadRequestException(ExceptionEnum.INVALID_TOKEN_ERR);
        }

        String address = parserPlatformAddress(params.getTokenName());
        if(address == null){
            throw new BadRequestException(ExceptionEnum.INVALID_TOKEN_ERR);
        }

        for (WithdrawRequest withdrawRequest : params.getRequest()) {
            //Cache this order basis info.
            transactionManageService.cacheTransactionOrder(withdrawRequest);

            log.info("Start submitting a transfer request.");

            CompletableFuture<TransactionReceipt> future = myToken
                    .transfer(withdrawRequest.getAddress() ,withdrawRequest.getAmount().toBigInteger().multiply(Const._UNIT))
                    .sendAsync();
            log.info("Transaction request submitted. Start listening thread.");

            new Thread(() -> {
                try {
                    TransactionReceipt receipt = future.get();
                    log.info("The transaction has been confirmed. >>>> :[{}]", JSON.toJSONString(receipt));
                    eventService.addSuccessEvent(receipt, withdrawRequest);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }).start();
            log.info("Transfer successful. >>> Waiting for blockchain confirmation transaction.");
        }
    }

    private List<WithdrawRequest> validOrderRequest(List<WithdrawRequest> withdrawRequests) {

        List<WithdrawRequest> validList = new ArrayList<>();

        for (WithdrawRequest withdrawRequest : withdrawRequests) {
            try {
                Assert.notNull(withdrawRequest.getAddress(),"Transfer to address must not be null.");
                Assert.notNull(withdrawRequest.getAmount(),"Transfer amount must not be null.");
                Assert.isTrue(withdrawRequest.getAmount().compareTo(BigDecimal.ZERO) > 0,"Negative or zero amount are not allowed");
                validList.add(withdrawRequest);
            } catch (Exception e) {
                log.error(e.getMessage());
                log.error("Throw withdraw order info:[{}]",JSON.toJSONString(withdrawRequest));
                eventService.addErrEvent(withdrawRequest,e.getMessage());
            }
        }
        return validList;

    }

    @Override
    public BalanceResponse getTokenBalance(@NonNull String address,@NonNull String tokenType) {
        try {
            Assert.notNull(address,"Wallet address must not be null.");
            IToken iToken = ContractMapper.get(tokenType);
            if(iToken == null){
                throw new Exception("Exception TokenType");
            }
            //Load contract client.
            BigInteger balance = iToken.balanceOf(address).send();
            return new BalanceResponse(balance.divide(Const._UNIT));
        }catch (Exception e){
            log.error("Query contract address balance throw error. >>> [{}]",e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    @NonNull
    @Override
    public BalanceResponse getETHBalance(@NonNull String address) {
        try {
            EthGetBalance balance = web3j.ethGetBalance(address, DefaultBlockParameter.valueOf("latest")).send();
            return new BalanceResponse(balance.getBalance());
        } catch (IOException e) {
            log.error("Query ETH balance throw error. >>> [{}]",e.getMessage());
            throw new BadRequestException(ExceptionEnum.BAD_REQUEST_ERR);
        }
    }

    @Override
    public Boolean transferEth(String toAddress, BigDecimal amount) {

        //Valid system wallet account balance is it enough.
        BigInteger systemWalletBalance = getETHBalance(ApplicationConfig.walletETHAddress).getBalance();

        Assert.isTrue(amount.compareTo(new BigDecimal(systemWalletBalance)) < 0,"Insufficient available balance in system account");

        Credentials credentials = LightWallet.buildDefaultCredentials();

        try {
            TransactionReceipt transactionReceipt = Transfer.sendFunds(
                    web3j, credentials, toAddress,
                    amount, Convert.Unit.WEI).send();
            log.info("Eth transfer successful. transactionReceipt:{}",JSON.toJSONString(transactionReceipt));
        } catch (Exception e) {
            log.error("Eth wallet transfer throw error. >>> {}",e.getMessage());
            log.error("Basis info params. toAddress:[{}] amount:[{}]",toAddress,amount);
            e.printStackTrace();
            throw new BadRequestException(ExceptionEnum.BAD_REQUEST_ERR);
        }
        return true;
    }

    @Override
    public GasResponse getEthGas() {
        EthGasPrice ethGasPrice;
        try {
            ethGasPrice = web3j.ethGasPrice().sendAsync().get();
            return new GasResponse(ethGasPrice.getGasPrice());
        } catch (Exception e) {
            log.error("Get eth recent gas price throw error. message:[{}]",e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public BalanceResponse getPlatformBalance(@NonNull String chain,@NonNull String tokenName) {

        Optional<ChainEnum> optionalChainEnum = ChainEnum.find(chain);
        if(!optionalChainEnum.isPresent()) throw new BadRequestException(ExceptionEnum.INVALID_TOKEN_ERR);
        if(!optionalChainEnum.get().isValid()) throw new BadRequestException(ExceptionEnum.NOT_SUPPORT_ERR);

        //Get token platform wallet address.
        String address = parserPlatformAddress(tokenName,chain);
        if(address == null) throw new BadRequestException(ExceptionEnum.INVALID_TOKEN_ERR);

        //If current request chain == tokenName. And is ETH.
        if(chain.equals(tokenName) && chain.equals("ETH")){
            return getETHBalance(address);
        }else{
            return getTokenBalance(address,tokenName);
        }
    }

    public String parserPlatformAddress(String tokenName,String chain) {

        boolean isChain = tokenName.equals(chain);

        List<TokenConfigs.TokenConfig> configs = tokenConfigs.getTokenConfigs();

        for (TokenConfigs.TokenConfig config : configs) {
            if(!chain.equals(config.getToken_type())){
                continue;
            }
            if(isChain){
                return config.getAddress();
            }
            List<TokenConfigs.TokenConfig.ChildToken> childTokens = config.getChild_tokens();
            for (TokenConfigs.TokenConfig.ChildToken childToken : childTokens) {
                if(childToken.getToken_name().equals(tokenName)){
                    return childToken.getAddress();
                }
            }
        }
        return null;
    }

    public String parserPlatformAddress(String tokenName){
        List<TokenConfigs.TokenConfig> configs = tokenConfigs.getTokenConfigs();

        for (TokenConfigs.TokenConfig config : configs) {
            if(config.getToken_type().equals(tokenName)){
                return config.getAddress();
            }
            List<TokenConfigs.TokenConfig.ChildToken> childTokens = config.getChild_tokens();
            for (TokenConfigs.TokenConfig.ChildToken childToken : childTokens) {
                if(childToken.getToken_name().equals(tokenName)){
                    return childToken.getAddress();
                }
            }
        }
        return null;
    }

    private BigInteger getTokenBalanceOfAddress(String address) throws ExecutionException, InterruptedException {
        Token myToken = LightWallet.loadTokenClient(web3j);
        return myToken.balanceOf(address).sendAsync().get();
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
    }

}
