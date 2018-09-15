package com.beeasy.rpc;

import com.alibaba.fastjson.JSON;
import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Util;
import feign.codec.Decoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.Reader;


public class RPCConfig {

    @Value("${rpc.secret}")
    String secret;

    @Bean
    RequestInterceptor getRI() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                requestTemplate.header("Token", secret);
            }
        };
    }

    @Bean
    public Decoder getDecoder(){
        return ((response, type) -> {
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
            Object obj = JSON.parseObject(str, type);
//            if (obj instanceof CloudBaseResponse) {
//                List<String> cookies = (List<String>) response.headers().getOrDefault("set-cookie", new ArrayList<>());
//                ((CloudBaseResponse) obj).setResponseCookies(cookies);
//            }
            return obj;
        });
    }


//    @Bean
//    Request.Options feignOptions() {
//        return new Request.Options(/**connectTimeoutMillis**/2 * 1000, /** readTimeoutMillis **/5 * 1000);
//    }
}
