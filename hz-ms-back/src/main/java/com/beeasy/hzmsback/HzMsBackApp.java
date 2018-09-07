package com.beeasy.hzmsback;

import org.beetl.sql.core.SQLManager;
import org.osgl.$;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class HzMsBackApp {

    @Autowired
    SQLManager sm;

    public static SQLManager sqlManager = null;

    @PostConstruct
    public void onStart(){
        if($.isNotNull(sm)){
            sqlManager = sm;
        }
    }

    public static void main(String[] args){
        SpringApplication.run(HzMsBackApp.class, args);
    }

}
