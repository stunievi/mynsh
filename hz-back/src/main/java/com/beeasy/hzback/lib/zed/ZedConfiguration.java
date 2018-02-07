package com.beeasy.hzback.lib.zed;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class ZedConfiguration {

    @Bean
    public Zed zed(){

        return new Zed();
    }
}
