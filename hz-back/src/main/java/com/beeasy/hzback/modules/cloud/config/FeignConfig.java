package com.beeasy.hzback.modules.cloud.config;


import com.alibaba.fastjson.JSON;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.cloud.response.CloudBaseResponse;
import feign.*;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.*;

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

    public static Map<Long,String> cookies = Collections.synchronizedMap(new HashMap<>());

    @Bean
    RequestInterceptor getRI(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                String cookie = cookies.get(Utils.getCurrentUserId());
                if(null != cookie){
                    requestTemplate.header("cookie",cookie);
                }
            }
        };
    }

    @Bean
    public Encoder multipartFormEncoder() {
        return new SpringFormEncoder(){
            @Override
            public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
                if (!bodyType.equals(MultipartFile.class)) {
                    super.encode(object, bodyType, template);
                } else {
                    Map<String, Object> data = Collections.singletonMap("filedata", object);
                    super.encode(data, MAP_STRING_WILDCARD, template);
                }
            }
        };
    }

//    @Bean
//    public MapFormHttpMessageConverter mapFormHttpMessageConverter(MultipartFileHttpMessageConverter multipartFileHttpMessageConverter) {
//        MapFormHttpMessageConverter mapFormHttpMessageConverter = new MapFormHttpMessageConverter();
//        mapFormHttpMessageConverter.addPartConverter(multipartFileHttpMessageConverter);
//        return mapFormHttpMessageConverter;
//    }
////
//    @Bean
//    public MultipartFileHttpMessageConverter multipartFileHttpMessageConverter() {
//        return new MultipartFileHttpMessageConverter();
//    }
//    @Bean
//    public Encoder feignFormEncoder() {
//        return new SpringFormEncoder();
//    }

//    @Bean
//    public Encoder feignEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
//        return new FeignSpringFormEncoder(messageConverters);
//    }

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
                if(!str.startsWith("{")){
                    return str;
                }
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
