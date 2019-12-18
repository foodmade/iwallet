package com.qkl.wallet.common;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public class UtilsService {

    public static String toDecimal(int decimal,BigInteger integer){
        StringBuilder sbf = new StringBuilder("1");
        for (int i = 0; i < decimal; i++) {
            sbf.append("0");
        }
        return new BigDecimal(integer).divide(new BigDecimal(sbf.toString()), 18, BigDecimal.ROUND_DOWN).toPlainString();
    }

    public static boolean isEth(String tokenName){
        return "ETH".equals(tokenName);
    }
}
