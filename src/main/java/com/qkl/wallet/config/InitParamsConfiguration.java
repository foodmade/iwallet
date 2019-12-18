package com.qkl.wallet.config;

import com.qkl.wallet.common.tools.IOCUtils;
import com.qkl.wallet.common.walletUtil.WalletUtils;
import com.qkl.wallet.core.manage.ScriptManage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author Jackies
 * @Date 2019/12/18 10:43
 * @Description TODO://钱包服务基本参数初始化配置器
 **/
@Order(1)
@Component
@Slf4j
public class InitParamsConfiguration {

    @Autowired
    private Config config;

    @PostConstruct
    public void initParamsConfig(){
        //所有的基本参数配置都从这儿写入,配置来源于config.json
        WalletUtils.initBasisConfig(config);
        //初始化lua脚本
        loadLuaCallScript();
    }

    private void loadLuaCallScript() {
        ScriptManage.getInstance();
    }

}
