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

        private List<ChildToken> child_tokens;

        @Data
        public static class ChildToken{

            private Integer id;

            private String token_name;

            private String contract_address;

            private String secretKey;

            private String contract_class_path;

            private Boolean valid;
        }
    }
}
