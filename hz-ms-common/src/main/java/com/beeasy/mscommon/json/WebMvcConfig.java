package com.beeasy.mscommon.json;

import com.alibaba.fastjson.serializer.*;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.beeasy.mscommon.json.FJHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.removeIf(httpMessageConverter -> httpMessageConverter instanceof MappingJackson2HttpMessageConverter); // 删除MappingJackson2HttpMessageConverter
        //1.需要定义一个Convert转换消息的对象
        converters.add(fastJsonHttpMessageConverter());
//        converters.add(new GsonHttpMessageConverter()); // 添加GsonHttpMessageConverter
    }

    @Bean
    public FJHttpMessageConverter fastJsonHttpMessageConverter(){
        FJHttpMessageConverter fastConverter = new FJHttpMessageConverter();
        //2.添加fastjson的配置信息，比如是否要格式化返回的json数据
//
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat,SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteDateUseDateFormat);

        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        fastConverter.setSupportedMediaTypes(fastMediaTypes);

        //3.在convert中添加配置信息
        fastConverter.setFastJsonConfig(fastJsonConfig);

        //配置long越界的问题
        SerializeConfig.getGlobalInstance().put(Long.class,ToStringSerializer.instance);
        SerializeConfig.getGlobalInstance().put(Long.TYPE,ToStringSerializer.instance);
        SerializeConfig.getGlobalInstance().put(BigInteger.class,ToStringSerializer.instance);
        return fastConverter;
    }
}
