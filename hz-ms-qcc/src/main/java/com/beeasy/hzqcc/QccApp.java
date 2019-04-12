package com.beeasy.hzqcc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EnableAsync
@SpringBootApplication(scanBasePackages = "com.beeasy",exclude={MongoAutoConfiguration.class})
@EnableScheduling
public class QccApp {
    static ExecutorService pool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(QccApp.class, args);

        //yibu
        pool.execute(() -> {
//            dasfafdsfafasfdsafdsa
//
//

        });

    }

}
