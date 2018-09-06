package com.beeasy.hzmsback;

import org.beetl.sql.core.SQLManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HzMsBackApp {

    @Autowired
    SQLManager sqlManager;

    public static void main(String[] args){
        SpringApplication.run(HzMsBackApp.class, args);
    }

}
