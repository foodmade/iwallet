package com.qkl.wallet.common.tools;

import com.qkl.wallet.common.cache.RedisUtil;
import com.qkl.wallet.common.SpringContext;
import com.qkl.wallet.config.Config;
import com.qkl.wallet.config.TokenConfigs;
import com.qkl.wallet.service.WalletService;
import org.web3j.protocol.Web3j;

/**
 * @Author Jackies
 * @Date 2019/12/15 18:45
 * @Description TODO://
 **/
public class IOCUtils {

    public static WalletService getWalletService(){
        return SpringContext.getBean(WalletService.class);
    }

    public static RedisUtil _Get_Redis(){
        return SpringContext.getBean(RedisUtil.class);
    }

    public static Web3j getWeb3j(){
        return SpringContext.getBean(Web3j.class);
    }

    public static Config getConfig() {
        return SpringContext.getBean(Config.class);
    }

    public static TokenConfigs getTokenConfigs(){
        return SpringContext.getBean(TokenConfigs.class);
    }
}
