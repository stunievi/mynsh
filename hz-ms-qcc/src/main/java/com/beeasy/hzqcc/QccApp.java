package com.beeasy.hzqcc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@SpringBootApplication(scanBasePackages = "com.beeasy",exclude={MongoAutoConfiguration.class})
@EnableScheduling
public class QccApp {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(QccApp.class, args);
    }

}
