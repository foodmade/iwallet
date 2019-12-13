package com.qkl.wallet.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class HttpUtils {

    /**
     * Post request.
     */
    public static ResponseEntity<JSONObject> postForEntity(String url, Map body){
        RestTemplate restTemplate = getRestTemplate();
        HttpEntity<MultiValueMap<String, String>> map = buildHttpEntity(body);
        log.info("Callback server. url:[{}] body:[{}]",url, JSON.toJSONString(map));
        return restTemplate.postForEntity(url,map,JSONObject.class);
    }

    public static ResponseEntity<JSONObject> postForEntity(String url,Object body){
        RestTemplate restTemplate = getRestTemplate();
        log.info("Callback server. url:[{}] body:[{}]",url, JSON.toJSONString(body));
        return restTemplate.postForEntity(url,body,JSONObject.class);
    }

    private static RestTemplate getRestTemplate(){
        return SpringContext.getApplicationContext().getBean(RestTemplate.class);
    }

    private static HttpEntity<MultiValueMap<String, String>> buildHttpEntity(Map map){
        MultiValueMap<String,String> multiValueMap = new LinkedMultiValueMap<>();
        if(map != null && !map.isEmpty()){
            map.keySet().parallelStream().forEach(t -> multiValueMap.set(t+"",map.get(t)+""));
        }
        return new HttpEntity<>(multiValueMap, buildHeaders());
    }

    private static HttpHeaders buildHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }


}
