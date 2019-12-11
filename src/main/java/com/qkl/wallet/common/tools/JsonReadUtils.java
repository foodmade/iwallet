package com.qkl.wallet.common.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * @Author xiaom
 * @Date 2019/12/11 16:43
 * @Version 1.0.0
 * @Description <>
 **/
@Slf4j
public class JsonReadUtils {

    public static <T> T readJsonFromClassPath(String path, Type type) throws IOException {

        ClassPathResource resource = new ClassPathResource(path);
        if (resource.exists()) {
            return JSON.parseObject(resource.getInputStream(), StandardCharsets.UTF_8, type,
                    // 自动关闭流
                    Feature.AutoCloseSource,
                    // 允许注释
                    Feature.AllowComment,
                    // 允许单引号
                    Feature.AllowSingleQuotes,
                    // 使用 Big decimal
                    Feature.UseBigDecimal);
        } else {
            log.warn("Warn notice::::: \t Token json file not found. Please check if the configuration is correct.");
            throw new IOException("Not found token.json.");
        }
    }

}
