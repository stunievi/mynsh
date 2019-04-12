package com.beeasy.hzqcc.config;

import com.beeasy.hzqcc.service.MongoService;
import com.beeasy.hzqcc.service.QccService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QccConfig {


    @Bean(name = "qcc1")
    public MongoService getQcc1(){
        MongoService service = new MongoService();
        service.start("databaseName");
        return service;
    }

    @Bean(name = "qcc2")
    public MongoService getQcc2(){
        MongoService service = new MongoService();
        service.start("db2");
        return service;
    }
}
