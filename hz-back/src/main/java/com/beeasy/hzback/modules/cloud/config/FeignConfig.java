package com.beeasy.hzback.modules.cloud.config;


import com.alibaba.fastjson.JSON;
import com.beeasy.hzback.modules.cloud.response.CloudBaseResponse;
import feign.*;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FeignConfig {
//    @Bean
//    public Feign.Builder feignBuilder() {
//        return Feign.builder().requestInterceptor(new RequestInterceptor() {
//            @Override
//            public void apply(RequestTemplate requestTemplate) {
//                requestTemplate.header("a", "b");
//            }
//        });
//    }

    public static String cookie = "";

    public synchronized static String getCookie() {
        return cookie;
    }

    public synchronized static void setCookie(String cookie) {
        FeignConfig.cookie = cookie;
    }

    @Bean
    RequestInterceptor getRI(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                if(!StringUtils.isEmpty(cookie)) {
                    requestTemplate.header("cookie",cookie);
                }
            }
        };
    }
    @Bean
    public Decoder getDecoder(){
        return new Decoder() {
            @Override
            public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
                if (response.status() == 404) return Util.emptyValueOf(type);
                if (response.body() == null) return null;
                Reader reader = response.body().asReader();
                if (!reader.markSupported()) {
                    reader = new BufferedReader(reader, 1);
                }
                char[] bytes = new char[1024];
                int len = -1;
                StringBuffer sb = new StringBuffer();
                while ((len = reader.read(bytes)) > -1) {
                    sb.append(bytes, 0, len);
                }
                String str = sb.toString();
                Object obj = JSON.parseObject(str,type);
                if(obj instanceof CloudBaseResponse){
                    List<String> cookies = (List<String>) response.headers().getOrDefault("set-cookie",new ArrayList<>());
                    ((CloudBaseResponse) obj).setResponseCookies(cookies);
                }
                return obj;
            }
        };
    }




}
