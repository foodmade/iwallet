package com.qkl.wallet;

import com.qkl.wallet.common.walletUtil.WalletUtils;
import com.qkl.wallet.vo.out.CreateWalletResponse;
import org.junit.Test;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;

import java.math.BigInteger;

/**
 * @Author Jackies
 * @Date 2019/12/15 15:46
 * @Description TODO://
 **/
public class CommonTest extends WalletApplicationTests {

    @Autowired
    private Web3j web3j;

    @Test
    public void testBlockNumber(){
        CreateWalletResponse response = new CreateWalletResponse("0xdF67ab61A941f4001a95255c57f586e7f99421f9","TEST_WALLET","86030747098382DD1F94C513E6B4AF9EADA76B75ADEF08CA96864DD14382A18D");
        WalletUtils.saveWalletInfo(response);

    }

}
