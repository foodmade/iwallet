package com.qkl.wallet.service.impl;

import com.alibaba.fastjson.JSON;
import com.qkl.wallet.common.Const;
import com.qkl.wallet.common.enumeration.ChainEnum;
import com.qkl.wallet.common.enumeration.ExceptionEnum;
import com.qkl.wallet.common.exception.BadRequestException;
import com.qkl.wallet.common.walletUtil.LightWallet;
import com.qkl.wallet.common.walletUtil.WalletUtils;
import com.qkl.wallet.common.walletUtil.outModel.WalletAddressInfo;
import com.qkl.wallet.config.ApplicationConfig;
import com.qkl.wallet.config.TokenConfigs;
import com.qkl.wallet.contract.IToken;
import com.qkl.wallet.contract.Token;
import com.qkl.wallet.core.ContractMapper;
import com.qkl.wallet.core.manage.OrderManage;
import com.qkl.wallet.service.WalletService;
import com.qkl.wallet.vo.in.BalanceParams;
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
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
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
    private TokenConfigs tokenConfigs;
    @Autowired
    private WalletService walletService;

    @Override
    public CreateWalletResponse createWallet() {
        try {
            WalletAddressInfo walletAddressInfo = LightWallet.createNewWallet(ApplicationConfig.defaultPassword);
            Credentials credentials = LightWallet.openWallet(ApplicationConfig.defaultPassword, walletAddressInfo.getName());
            String privateKey = Numeric.toHexStringNoPrefix(credentials.getEcKeyPair().getPrivateKey());
            CreateWalletResponse response = new CreateWalletResponse(credentials.getAddress(),walletAddressInfo.getName(),privateKey);
            //Save the created wallet address information
            WalletUtils.saveWalletInfo(response);
            return response;
        } catch (Exception e) {
            log.error("Create wallet throw error . throw info >>> [{}]",e.getMessage());
            return null;
        }
    }

    @Override
    public WithdrawResponse withdraw(WithdrawParams params) throws IOException {

        if(!validOrderRequest(params)){
            log.info("The order does not meet the conditions, and all the orders have been rejected");
            throw new BadRequestException("存在不满足条件的订单,请检查");
        }

        //判断是不是BTC或者ETH主链之间的交易
        if(params.getTokenName() == null){
            //如果子链参数为空,则说明这是BTC或者ETH之间的交易,分发给主链交易流程
            OrderManage.addChainOrder(params);
        }else{
            OrderManage.addTokenOrder(params);
        }
        return new WithdrawResponse("");
    }


    private boolean validOrderRequest(WithdrawParams params) {
        for (WithdrawRequest withdrawRequest : params.getRequest()) {
            try {
                Assert.notNull(withdrawRequest.getAddress(), "Transfer to address must not be null.");
                Assert.notNull(withdrawRequest.getAmount(), "Transfer amount must not be null.");
                Assert.isTrue(withdrawRequest.getAmount().compareTo(BigDecimal.ZERO) > 0, "Negative or zero amount are not allowed");
            } catch (Exception e) {
                log.error(e.getMessage());
                log.error("Throw withdraw order info:[{}]", JSON.toJSONString(withdrawRequest));
                return false;
            }
        }


        //获取平台钱包地址
        String formAddress = foundPlatformAddress(params.getTokenName(),params.getChain());
        if(formAddress == null){
            throw new BadRequestException(ExceptionEnum.INVALID_TOKEN_ERR);
        }

        return true;
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
            return new BalanceResponse(WalletUtils.unitCover(balance,tokenType));
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
            return new BalanceResponse(WalletUtils.unitEthCover(balance.getBalance()));
        } catch (IOException e) {
            log.error("Query ETH balance throw error. >>> [{}]",e.getMessage());
            throw new BadRequestException(ExceptionEnum.BAD_REQUEST_ERR);
        }
    }

    @Override
    public Boolean transferEth(String toAddress, BigDecimal amount) {
        transferEth(ApplicationConfig.walletETHAddress,toAddress,amount,ApplicationConfig.secretKey);
        return true;
    }

    @Override
    public Boolean transferEth(String fromAddress, String toAddress, BigDecimal amount) {
        if(fromAddress == null){
            //如果打款地址是空,则默认使用平台钱包地址打款
            fromAddress = ApplicationConfig.walletETHAddress;
        }
        //根据钱包地址查找对应的秘钥
        String secretKey = walletService.foundTokenSecretKey(fromAddress);
        transferEth(fromAddress, toAddress, amount,secretKey);
        return true;
    }

    @Override
    public TransactionReceipt transferEth(String fromAddress, String toAddress, BigDecimal amount, String secretKey) {
        //Valid system wallet account balance is it enough.
        BigDecimal systemWalletBalance = getETHBalance(fromAddress).getBalance();

        log.info("ETH transferEth function process........................");

        Assert.isTrue(amount.compareTo(systemWalletBalance) < 0,"Insufficient available balance in system account");

        Credentials credentials = LightWallet.buildCredentials(secretKey);
        try {
            TransactionReceipt transactionReceipt = Transfer.sendFunds(
                    web3j, credentials, toAddress,
                    amount.multiply(new BigDecimal(Const._ETH_TOKEN_UNIT)), Convert.Unit.WEI).send();
            log.info("Eth transfer successful. transactionReceipt:{}",JSON.toJSONString(transactionReceipt));
            return transactionReceipt;
        } catch (Exception e) {
            log.error("Eth wallet transfer throw error. >>> {}",e.getMessage());
            log.error("Basis info params. toAddress:[{}] amount:[{}]",toAddress,amount);
            e.printStackTrace();
            throw new BadRequestException(ExceptionEnum.BAD_REQUEST_ERR);
        }
    }

    @Override
    public GasResponse getEthGasResponse() {
        EthGasPrice ethGasPrice;
        try {
            ethGasPrice = getGasPrice();
            log.info("获取到的gas:{}", ethGasPrice.getGasPrice());
            return new GasResponse(WalletUtils.unitCover(ethGasPrice.getGasPrice()));
        } catch (Exception e) {
            log.error("Get eth recent gas price throw error. message:[{}]",e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    public EthGasPrice getGasPrice() {
        try {
            return web3j.ethGasPrice().send();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public BalanceResponse getPlatformBalance(BalanceParams balanceParams) {

        String chain = balanceParams.getChain();
        String tokenName = balanceParams.getTokenName();

        Optional<ChainEnum> optionalChainEnum = ChainEnum.find(chain);
        if(!optionalChainEnum.isPresent()) throw new BadRequestException(ExceptionEnum.INVALID_TOKEN_ERR);
        if(!optionalChainEnum.get().isValid()) throw new BadRequestException(ExceptionEnum.NOT_SUPPORT_ERR);

        String address;

        if(balanceParams.getAddress() == null){
            //Get token platform wallet address.
            address = foundPlatformAddress(tokenName,chain);
            if(address == null) throw new BadRequestException(ExceptionEnum.INVALID_TOKEN_ERR);
        }else{
            address = balanceParams.getAddress();
        }

        //If current request chain == tokenName. And is ETH.
        if(chain.equals(tokenName) && chain.equals("ETH")){
            return getETHBalance(address);
        }else{
            return getTokenBalance(address,tokenName);
        }
    }

    public String foundPlatformAddress(String tokenName, String chain) {

        if(tokenName == null){
            return foundPlatformAddress(chain);
        }

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

    public String foundPlatformAddress(String keyName){
        List<TokenConfigs.TokenConfig> configs = tokenConfigs.getTokenConfigs();

        for (TokenConfigs.TokenConfig config : configs) {
            if(config.getToken_type().equals(keyName)){
                return config.getAddress();
            }
            List<TokenConfigs.TokenConfig.ChildToken> childTokens = config.getChild_tokens();
            for (TokenConfigs.TokenConfig.ChildToken childToken : childTokens) {
                if(childToken.getToken_name().equals(keyName)){
                    return childToken.getAddress();
                }
            }
        }
        return null;
    }

    public String foundTokenSecretKey(String address){
        //先从token.json查找,如果未查询到,再根据redis缓存获取
        String key = foundTokenSecretKeyWithJsonConfig(address);
        if(key != null && !"".equals(key)){
            return key;
        }
        //从redis缓存获取
        return WalletUtils.getKeySecretByAddress(address);
    }

    private String foundTokenSecretKeyWithJsonConfig(String address){
        List<TokenConfigs.TokenConfig> configs = tokenConfigs.getTokenConfigs();

        for (TokenConfigs.TokenConfig config : configs) {
            if(config.getAddress().equals(address)){
                return config.getSecretKey();
            }
            List<TokenConfigs.TokenConfig.ChildToken> childTokens = config.getChild_tokens();
            for (TokenConfigs.TokenConfig.ChildToken childToken : childTokens) {
                if(childToken.getAddress().equals(address)){
                    return childToken.getSecretKey();
                }
            }
        }
        return null;
    }

    @Override
    public String foundPlatformContractAddress(String tokenName) {

        List<TokenConfigs.TokenConfig> configs = tokenConfigs.getTokenConfigs();
        for (TokenConfigs.TokenConfig config : configs) {
            List<TokenConfigs.TokenConfig.ChildToken> childTokens = config.getChild_tokens();
            for (TokenConfigs.TokenConfig.ChildToken childToken : childTokens) {
                if(childToken.getToken_name().equals(tokenName)){
                    return childToken.getContract_address();
                }
            }
        }
        return null;
    }

    @Override
    public String foundPlatformTokenName(String contractAddress) {

        List<TokenConfigs.TokenConfig> configs = tokenConfigs.getTokenConfigs();

        for (TokenConfigs.TokenConfig config : configs) {
            List<TokenConfigs.TokenConfig.ChildToken> childTokens = config.getChild_tokens();
            for (TokenConfigs.TokenConfig.ChildToken childToken : childTokens) {
                if(childToken.getContract_address().toLowerCase().equals(contractAddress)){
                    return childToken.getToken_name();
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

    public boolean validTransferStatus(String hash) {

        Optional<TransactionReceipt> optional;
        try {
            optional = web3j.ethGetTransactionReceipt(hash).send().getTransactionReceipt();
            if(!optional.isPresent()){
                return false;
            }
            String statusHex = optional.get().getStatus();
            log.info("Status :[{}]",statusHex);
            return Const._SUCCESS_HEX.equals(statusHex);
        } catch (IOException e) {
            log.error("Check for abnormal transaction status. message:{}",e.getMessage());
            return false;
        }
    }

    @Override
    public EthTransaction fetchTransactionInfoByHash(String hash) {
        try {
            return web3j.ethGetTransactionByHash(hash).send();
        } catch (IOException e) {
            log.error("Fetch transaction detail info throw error:[{}]",e.getMessage());
            return null;
        }
    }

    @Override
    public Long foundDecimalsByTokenName(String tokenName) {
        List<TokenConfigs.TokenConfig> configs = tokenConfigs.getTokenConfigs();

        for (TokenConfigs.TokenConfig config : configs) {
            if(config.getToken_type().equals(tokenName)){
                return config.getDecimals();
            }
            List<TokenConfigs.TokenConfig.ChildToken> childTokens = config.getChild_tokens();
            for (TokenConfigs.TokenConfig.ChildToken childToken : childTokens) {
                if(childToken.getToken_name().equals(tokenName)){
                    return childToken.getDecimals();
                }
            }
        }
        return null;
    }
}
