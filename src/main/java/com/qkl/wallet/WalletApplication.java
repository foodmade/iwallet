package com.qkl.wallet;

import com.qkl.wallet.config.ApplicationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WalletApplication {

    public static void main(String[] args) {
        SpringApplication.run(WalletApplication.class, args);

        System.out.println("env:"+System.getenv("bar"));
    }

}
