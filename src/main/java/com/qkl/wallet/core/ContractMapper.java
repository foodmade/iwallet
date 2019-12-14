package com.qkl.wallet.core;

import com.qkl.wallet.common.enumeration.ExceptionEnum;
import com.qkl.wallet.common.exception.ServerException;
import com.qkl.wallet.contract.IToken;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author xiaom
 * @Date 2019/12/11 16:31
 * @Version 1.0.0
 * @Description <Contract loaders. 通过resource/Token.json配置文件反解析所有合法的合约加载器>
 **/
@Slf4j
public class ContractMapper {

    private static HashMap<String,Object> contractLoaderMaps = new HashMap<>();

    private static HashMap<String,ContractMethodInvokeParams> contractMethodInvokeParamsHashMap = new HashMap<>();


    /**
     * Contract example load handler.
     * @param contractName  Contract name.
     * @param method        Load contract method.
     * @param params        Invoke need params.
     */
    public static void putInvokeExample(String contractName,Method method,Object[] params ){
        contractMethodInvokeParamsHashMap.put(contractName,new ContractMethodInvokeParams(params,method));
    }

    /**
     * Get contract Object.
     */
    public static IToken get(String contractName){
        ContractMethodInvokeParams invokeParams = contractMethodInvokeParamsHashMap.get(contractName);
        Assert.notNull(invokeParams,"This contract loader is not instantiated. Contract name :["+contractName+"]");

        try {
            return (IToken) invokeParams.getMethod().invoke(null,invokeParams.getInvokeParams());
        } catch (Exception e) {
            log.error("Invoke contract Object static load method failed. throw message:[{}]",e.getMessage());
            throw new ServerException(ExceptionEnum.INVOKE_METHOD_ERR);
        }
    }

    /**
     * Cache contract example.
     * @param contractName  Contract name.
     * @param loader        Contract loader object.
     */
    public static void put(String contractName,Object loader){
        contractLoaderMaps.put(contractName,loader);
    }


    public static Integer contractLoaderSize(){
        return contractMethodInvokeParamsHashMap.size();
    }

    /**
     * Get all valid contract name.
     */
    public static List<String> contractTypeList(){
        return new ArrayList<>(contractMethodInvokeParamsHashMap.keySet());
    }


    @Data
    public static class ContractMethodInvokeParams{

        //Execute java reflection static method params.
        private Object[] invokeParams = {};
        //Contract static method proxy.
        private Method method;

        public ContractMethodInvokeParams(Object[] invokeParams, Method method) {
            this.invokeParams = invokeParams;
            this.method = method;
        }
    }
}
