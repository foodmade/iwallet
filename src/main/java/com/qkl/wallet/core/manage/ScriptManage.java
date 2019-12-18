package com.qkl.wallet.core.manage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @Author Jackies
 * @Date 2019/12/18 11:23
 * @Description TODO://
 **/
@Slf4j
public class ScriptManage {

    //获取最后同步区块高度call lua脚本
    @Getter
    public RedisScript<Long> lastBlockNumberCallScript;

    public static ScriptManage _INSTALL;

    private ScriptManage(){
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        redisScript.setScriptSource(new ResourceScriptSource(new
                ClassPathResource("blockincrease.lua")));

        lastBlockNumberCallScript = redisScript;
    }

    public static ScriptManage getInstance(){
        if(_INSTALL == null){
            _INSTALL = new ScriptManage();
        }
        return _INSTALL;
    }
}
