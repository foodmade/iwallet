package com.qkl.wallet.common.tools;

import com.qkl.wallet.common.RedisUtil;
import com.qkl.wallet.common.SpringContext;
import com.qkl.wallet.service.WalletService;

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

}
