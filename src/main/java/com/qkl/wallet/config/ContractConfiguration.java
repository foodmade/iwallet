package com.qkl.wallet.config;

import com.qkl.wallet.common.SpringContext;
import com.qkl.wallet.common.tools.ReflectionUtils;
import com.qkl.wallet.common.walletUtil.LightWallet;
import com.qkl.wallet.core.ContractMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import javax.annotation.PostConstruct;
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
public class ContractConfiguration {

    private static final String PREFIX = "CONTRACT_LINK_LOADER";

    @Autowired
    private TokenConfigs tokenConfigs;

    @PostConstruct
    public void loadContractObject(){
        if(tokenConfigs == null){
            log.error("TokenConfigs example is null. Not executed loadContractObject method.");
            return;
        }
        log.info(">>>>>>>>>>>>>>>>>>>>>Start constructing the contract loader<<<<<<<<<<<<<<<<<<<<<<");
        moduleLog("Token json config parent node:[{}]. child:[{}]", tokenConfigs.getTokenConfigs().size(),countChild());

        tokenConfigs.getTokenConfigs().parallelStream().forEach(tokenConfig -> {
            if(!tokenConfig.getValid()){
                moduleLog("Current contract config. contract type:[{}] valid:[false]",tokenConfig.getToken_type());
                return;
            }
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
                    Object contractExample = Class.forName(childToken.getContract_class_path())
                            .getMethod("load"
                                    ,String.class
                                    ,Web3j.class, Credentials.class, ContractGasProvider.class)
                            .invoke(null,childToken.getContract_address(),SpringContext.getBean(Web3j.class),
                                    LightWallet.buildCredentials(childToken.getSecretKey()),new DefaultGasProvider());
                    //Valid token .
                    Object valid = ReflectionUtils.getAccessibleMethod(contractExample,"isValid").invoke(contractExample);

                    if(valid == null){
                        moduleLog("Check for valid status failed. TokenName:[{}] address:[{}]",childToken.getToken_name(),childToken.getContract_address());
                        continue;
                    }
                    if(!Boolean.parseBoolean(valid+"")){
                        moduleLog("Contract valid:[{}],TokenName:[{}] address:[{}]",valid,childToken.getToken_name(),childToken.getContract_address());
                        continue;
                    }
                    moduleLog("TokenName:[{}] address:[{}] valid:[{}]",childToken.getToken_name(),childToken.getContract_address(),valid);
                    ContractMapper.put(childToken.getToken_name(),contractExample);
                    moduleLog("-------------------------------------------------------------------------");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        moduleLog("Install all token contract example finish. Mapper size:[{}]",ContractMapper.contractLoaderSize());
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
