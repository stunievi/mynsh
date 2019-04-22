package com.beeasy.loadqcc.config;

import com.beeasy.loadqcc.service.MongoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QccConfig {

    @Bean(name = "qcc1")
    public MongoService getQcc1(){
        MongoService service = new MongoService();
        service.start("databaseName1");
        return service;
    }

    @Bean(name = "qcc2")
    public MongoService getQcc2(){
        MongoService service = new MongoService();
        service.start("databaseName2");
        return service;
    }
}