package com.qkl.wallet.config;

import com.qkl.wallet.common.cache.RedisUtil;
import com.qkl.wallet.common.enumeration.TokenEventEnum;
import com.qkl.wallet.common.tools.ReflectionUtils;
import com.qkl.wallet.common.walletUtil.LightWallet;
import com.qkl.wallet.common.walletUtil.WalletUtils;
import com.qkl.wallet.core.ContractMapper;
import com.qkl.wallet.core.transfer.work.TransactionMonitorThread;
import com.qkl.wallet.core.transfer.work.WorkFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author xiaom
 * @Date 2019/12/11 16:40
 * @Version 1.0.0
 * @Description <>
 **/
@Component
@Slf4j
@DependsOn("redisUtil")
public class ChainConfiguration {

    private static final String PREFIX = "CONTRACT_LINK_LOADER";

    @Autowired
    private TokenConfigs tokenConfigs;
    @Autowired
    private Web3j web3j;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private Config config;

    public void initializationContractConfiguration() throws Exception {
        //初始化区块基数(这个块高度只会初始化一次,以后需要回查账本的时候,就使用此区块高度为起点)
        initializationStartBlockNumber();

        //通过Token.json配置文件,反解析所有代币类型,并生成合约加载器.
        if(!loadContractObject()){
            return;
        }

        //启动区块监听线程
        new TransactionMonitorThread(config).start();

        //创建已验证合法的合约加载器工作线程.
        createContractWorkThread();
    }

    private void initializationStartBlockNumber() throws Exception {
        if(WalletUtils.getBasisBlockNumber(redisUtil) == null){
            //获取初始高度
            Long blockNumber = WalletUtils.getCurrentBlockNumber();
            if(blockNumber == null){
                throw new Exception("初始化区块基础高度失败,请重启服务器初始化");
            }
            WalletUtils.generateBasisBlockNumber(blockNumber);
        }
    }

    private void createContractWorkThread() {

        if(ContractMapper.contractLoaderSize() == 0){
            return;
        }

        ContractMapper
                .contractTypeList()
                .forEach(tokenName -> WorkFactory
                                    .build()
                                    .buildTokenThreadWork(tokenName, TokenEventEnum.find(tokenName).get(),redisUtil)
                                    .ifPresent(Thread::start));
    }


    public boolean loadContractObject(){
        if(tokenConfigs == null){
            log.error("TokenConfigs example is null. Not executed loadContractObject method.");
            return false;
        }

        log.info(">>>>>>>>>>>>>>>>>>>>>Start constructing the contract loader<<<<<<<<<<<<<<<<<<<<<<");
        moduleLog("Token json config parent node:[{}]. child:[{}]", tokenConfigs.getTokenConfigs().size(),countChild());

        tokenConfigs.getTokenConfigs().parallelStream().forEach(tokenConfig -> {
            if(!tokenConfig.getValid()){
                moduleLog("Current contract config. contract type:[{}] valid:[false]",tokenConfig.getToken_type());
                return;
            }

            //If it is the main chain, no contract address is required
            ContractMapper.putInvokeExample(tokenConfig.getToken_type(),null,null);

            List<TokenConfigs.TokenConfig.ChildToken> childTokens = tokenConfig.getChild_tokens();
            if(childTokens == null || childTokens.isEmpty()){
                moduleLog("Current contract config. Child token config empty. contract type:[{}]",tokenConfig.getToken_type());
                return;
            }

            moduleLog("Start load link contract. TokenName:[{}]",tokenConfig.getToken_type());
            for (TokenConfigs.TokenConfig.ChildToken childToken : childTokens) {
                if(!childToken.getValid()){
                    moduleLog("TokenName:[{}],parentName:[{}], valid:[{}]",childToken.getToken_name(),tokenConfig.getToken_type(),childToken.getValid());
                    continue;
                }
                if(childToken.getContract_class_path().isEmpty()){
                    moduleLog("Token object class loader path is null. tokenName:[{}] skip.",childToken.getToken_name());
                    continue;
                }
                moduleLog("Structure contract loader. tokenName:[{}],class loader path:[{}] ",childToken.getToken_name(),childToken.getContract_class_path());
                try {
                    //Load contract.
                    Method method = Class.forName(childToken.getContract_class_path())
                            .getMethod("load"
                                    ,String.class
                                    ,Web3j.class, Credentials.class, ContractGasProvider.class);

                    Object[] params = {childToken.getContract_address(),web3j,
                            LightWallet.buildCredentials(childToken.getSecretKey()),new DefaultGasProvider()};

                    Object contractExample = method.invoke(null,params);

                    //Valid token .
                    Object valid = ReflectionUtils.getAccessibleMethod(contractExample,"isValid").invoke(contractExample);

                    if(valid == null){
                        moduleLog("Check for valid status failed. TokenName:[{}] address:[{}]",childToken.getToken_name(),childToken.getContract_address());
                        continue;
                    }
                    if(!Boolean.parseBoolean(valid+"")){
                        moduleLog("Contract load failed. TokenName:[{}] address:[{}]",childToken.getToken_name(),childToken.getContract_address());
                        continue;
                    }
                    moduleLog("Contract load successful. TokenName:[{}] address:[{}]",childToken.getToken_name(),childToken.getContract_address());
                    ContractMapper.putInvokeExample(childToken.getToken_name(),method,params);
                    moduleLog("-------------------------------------------------------------------------");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        moduleLog("Install all token contract example finish. Mapper size:[{}]",ContractMapper.contractLoaderSize());
        return true;
    }

    private void moduleLog(String message,Object...vals){
        log.info(PREFIX + "\t\t:" + message,vals);
    }

    private Integer countChild(){
        if(tokenConfigs == null){
            return 0;
        }
        AtomicReference<Integer> count = new AtomicReference<>(0);
        tokenConfigs.getTokenConfigs().forEach(config -> count.updateAndGet(v -> v + config.getChild_tokens().size()));
        return count.get();
    }
}
