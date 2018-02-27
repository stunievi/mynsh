package com.beeasy.hzback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@EnableWebMvc
@EnableAsync
@SpringBootApplication(scanBasePackages = {"com.beeasy.hzback","bin.leblanc"})
public class Application{

    public static void main(String[] args){
        SpringApplication.run(Application.class,args);
    }


}