package com.qkl.wallet.common;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @Author xiaom
 * @Date 2019/11/22 11:00
 * @Version 1.0.0
 * @Description <>
 **/
public class HttpUtils {

    /**
     * Post request.
     */
    public static ResponseEntity<JSONObject> postForEntity(String url, Map body){
        RestTemplate restTemplate = SpringContext.getApplicationContext().getBean(RestTemplate.class);
        return restTemplate.postForEntity(url,buildHttpEntity(body),JSONObject.class);
    }

    private static HttpEntity<MultiValueMap<String, String>> buildHttpEntity(Map map){
        MultiValueMap<String,String> multiValueMap = new LinkedMultiValueMap<>();
        if(map != null && !map.isEmpty()){
            map.keySet().parallelStream().forEach(t -> multiValueMap.add(t+"",map.get(t)+""));
        }
        return new HttpEntity<>(multiValueMap, buildHeaders());
    }

    private static HttpHeaders buildHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }


}
