package com.qkl.wallet.config;

import lombok.Data;

import java.util.List;

/**
 * @Author Jackies
 * @Date 2019/12/10 22:55
 * @Description TODO://
 **/
@Data
public class TokenConfig {

    private String token_type;

    private String chain_host;

    private List<ChildToken> child_tokens;

    @Data
    private static class ChildToken{

        private Integer id;

        private String token_name;

        private String contract_address;

        private String default_secretKey;
    }
}
