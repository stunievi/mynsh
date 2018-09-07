package com.beeasy.hzmsfile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;

import javax.servlet.MultipartConfigElement;
import java.io.File;

@SpringBootApplication
public class HzFileApp {

    public static void main(String[] args){
        SpringApplication.run(HzFileApp.class, args);
    }
}
