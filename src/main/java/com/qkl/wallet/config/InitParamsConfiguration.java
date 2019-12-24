package com.qkl.wallet.config;

import com.qkl.wallet.common.cache.RedisUtil;
import com.qkl.wallet.common.walletUtil.WalletUtils;
import com.qkl.wallet.core.manage.ScriptManage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author Jackies
 * @Date 2019/12/18 10:43
 * @Description TODO://钱包服务基本参数初始化配置器
 **/
@Component
@Slf4j
@DependsOn("redisUtil")
public class InitParamsConfiguration {

    @Autowired
    private Config config;
    @Autowired
    private RedisUtil redisUtil;

    @PostConstruct
    public void initParamsConfig(){
        //所有的基本参数配置都从这儿写入,配置来源于config.json
        WalletUtils.initBasisConfig(config,redisUtil);
        //初始化lua脚本
        loadLuaCallScript();
    }

    private void loadLuaCallScript() {
        ScriptManage.getInstance();
    }

}
