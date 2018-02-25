package com.beeasy.hzback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@SpringBootApplication
@EnableWebMvc
@EnableAsync
@ComponentScan(basePackages = {"bin.leblanc","com.beeasy.hzback"})
public class Application{

    public static void main(String[] args){
        SpringApplication.run(Application.class,args);
    }


}