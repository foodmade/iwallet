package com.qkl.wallet.core;

import com.qkl.wallet.common.BeanUtils;
import com.qkl.wallet.contract.IToken;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * @Author xiaom
 * @Date 2019/12/11 16:31
 * @Version 1.0.0
 * @Description <Contract loaders. 通过resource/Token.json配置文件反解析所有合法的合约加载器>
 **/
@Slf4j
public class ContractMapper {

    private static HashMap<String,Object> contractLoaderMaps = new HashMap<>();

    /**
     * Cache contract example.
     * @param contractName  Contract name.
     * @param loader        Contract loader object.
     */
    public static void put(String contractName,Object loader){
        contractLoaderMaps.put(contractName,loader);
    }

    /**
     * Get contract Object.
     */
    public static IToken get(String contractName) throws Exception {
        Object loaderObj = contractLoaderMaps.get(contractName);
        if(loaderObj == null){
            throw new Exception("This contract loader is not instantiated. Contract name :["+contractName+"]");
        }
        try {
            return (IToken)loaderObj;
        }catch (Exception e){
            log.error("Get contract loader failed. Bean transFrom Object throw error. message:[{}]",e.getMessage());
        }
        return null;
    }

    public static Integer contractLoaderSize(){
        return contractLoaderMaps.size();
    }

}
