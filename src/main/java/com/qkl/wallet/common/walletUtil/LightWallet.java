package com.qkl.wallet.common.walletUtil;

import com.qkl.wallet.common.walletUtil.outModel.WalletAddressInfo;
import com.qkl.wallet.config.ApplicationConfig;
import com.qkl.wallet.contract.Token;
import lombok.extern.slf4j.Slf4j;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.tx.Contract;
import org.web3j.utils.Numeric;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * @Author xiaom
 * @Date 2019/11/21 15:02
 * @Version 1.0.0
 * @Description <>
 **/
@Slf4j
public class LightWallet {

    /**
     * 创建一个新的钱包
     *
     * @param password 钱包密码
     */
    public static WalletAddressInfo createNewWallet(String password) throws Exception {
        try {
            File file = new File(ApplicationConfig.wallPath);
            String name;
            String json;
            if (file.exists()) {
                name = WalletUtils.generateFullNewWalletFile(password, file);
                Path path = FileSystems.getDefault().getPath(ApplicationConfig.wallPath, name);
                byte[] b = java.nio.file.Files.readAllBytes(path);
                json = new String(b);
                return new WalletAddressInfo(name, json);
            } else {
                throw new Exception("Invalid WALLET_PATH " + ApplicationConfig.wallPath);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 打开钱包
     *
     * @param password
     * @param walletName
     * @return
     * @throws Exception
     */
    public static Credentials openWallet(String password, String walletName) throws Exception {
        Credentials credentials = WalletUtils.loadCredentials(password, ApplicationConfig.wallPath + walletName);
        log.info("私钥:{}", Numeric.toHexStringNoPrefix(credentials.getEcKeyPair().getPrivateKey()));
        log.info("公钥:{}", Numeric.toHexStringWithPrefixZeroPadded(credentials.getEcKeyPair().getPublicKey(), 64 << 1));
        log.info("地址:{}", credentials.getAddress());
        return credentials;
    }

    /**
     * 打开合约客户端
     */
    public static Token loadTokenClient(Web3j web3j){
        Credentials credentials = Credentials.create(ApplicationConfig.secretKey);
        //Load contract client.
        return Token.load(ApplicationConfig.contractAddress,web3j,credentials, Contract.GAS_PRICE,Contract.GAS_LIMIT);
    }

}
