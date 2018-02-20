package com.beeasy.hzback.lib.zed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Configuration
@Slf4j
public class ZedConfiguration implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    Zed zed;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("start ok");
        zed.init();


        /**
         *
         *
         * zed.addRole(roleName,checkRole,
         */
        zed.addRole("admin",(token) -> {
            return token.equals("su");
        },(role) -> {
            log.info("fuck");
            role.allowAllGet();
            role.allowAllPost();
            role.allowAllPut();
            role.allowAllDelete();
        });
    }

//    @Bean
//    public Zed zed(){
//        Zed zed = new Zed();
//        zed.init();
//        return zed;
//    }
}
