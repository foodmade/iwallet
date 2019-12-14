package com.qkl.wallet.core.event.listener;

import com.qkl.wallet.config.ApplicationConfig;
import com.qkl.wallet.core.event.ConfirmEvent;
import com.qkl.wallet.core.event.TransferEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @Author Jackies
 * @Date 2019/12/14 21:39
 * @Description TODO://
 **/
@Component
@Slf4j
public class ConfirmEventListener {

    private static final String callbackUrl = ApplicationConfig.callBackHost + "/admin/finance/callbackWallet";


    @EventListener
    public void onApplicationEvent(ConfirmEvent event) {

    }

}
