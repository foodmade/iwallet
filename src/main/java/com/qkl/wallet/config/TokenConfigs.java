package com.qkl.wallet.config;

import com.alibaba.fastjson.JSON;
import com.qkl.wallet.common.cache.JedisKey;
import com.qkl.wallet.common.cache.RedisUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @Author Jackies
 * @Date 2019/12/10 22:55
 * @Description TODO://
 **/
@Configuration
@Data
@Slf4j
public class TokenConfigs {

    @Autowired
    private RedisUtil redisUtil;

    @PostConstruct
    public void readTokenJsonConfig() {
        try {
            //Token配置文件改造为放入redis
            Object tokenObj = redisUtil.get(JedisKey._TOKEN_CONFIG_KEY);
            TokenConfigs tokenConfigs = JSON.parseObject(tokenObj.toString(),TokenConfigs.class);
            log.info("Token json load successful. \n Token json:[{}]", JSON.toJSONString(tokenConfigs));
            this.tokenConfigs = tokenConfigs.getTokenConfigs();
        } catch (Exception e) {
            log.error("Serious warning::::: \t Already found token json file. But read this file failed. throw error message:[{}]",e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    private List<TokenConfig> tokenConfigs;

    public String getEthPlatformHost(){
        return getEthTokenConfig().getChain_host();
    }

    public String getEthPlatformSecretKey(){
        return getEthTokenConfig().getSecretKey();
    }

    public String getEthPlatformAddress(){
        return getEthTokenConfig().getAddress();
    }

    private TokenConfig getEthTokenConfig(){
        return tokenConfigs.get(0);
    }

    @Data
    public static class TokenConfig{
        private String token_type;

        private String chain_host;

        private Boolean valid;

        private String address;

        private String secretKey;

        private List<ChildToken> child_tokens;
        /**
         * 小数位数
         */
        private Long decimals;

        @Data
        public static class ChildToken{

            private Integer id;
            /**
             * 代币名称
             */
            private String token_name;
            /**
             * 智能合约地址
             */
            private String contract_address;
            /**
             * 平台钱包秘钥
             */
            private String secretKey;
            /**
             * class path
             */
            private String contract_class_path;
            /**
             * 是否有效
             */
            private Boolean valid;
            /**
             * 平台钱包地址
             */
            private String address;
            /**
             * 代币小数位数
             */
            private Long decimals;
        }
    }
}
