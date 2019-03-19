package com.beeasy.loadqcc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@SpringBootApplication(scanBasePackages = "com.beeasy",exclude={MongoAutoConfiguration.class})
@EnableScheduling
public class LoadQccApp {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(LoadQccApp.class, args);
    }

}
