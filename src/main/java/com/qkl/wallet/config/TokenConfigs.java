package com.qkl.wallet.config;

import lombok.Data;

import java.util.List;

/**
 * @Author Jackies
 * @Date 2019/12/10 22:55
 * @Description TODO://
 **/
@Data
public class TokenConfigs {

    private List<TokenConfig> tokenConfigs;

    @Data
    public static class TokenConfig{
        private String token_type;

        private String chain_host;

        private Boolean valid;

        private String address;

        private List<ChildToken> child_tokens;

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
        }
    }
}
