package com.beeasy.hzback.lib.zed;

import com.beeasy.hzback.modules.setting.entity.User;
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
            return token.equals("SU");
        },(role) -> {
            log.info("fuck");
            role.allowAllGet();
            role.allowAllPost();
            role.allowAllPut();
            role.allowAllDelete();
        });

        zed.addRole("unknown",token -> {
            return token.equals("UNKNOWN");
        },role -> {
            role.disallowAllGet();
            role.disallowAllPut();
            role.disallowAllPost();
            role.disallowAllDelete();
        });


        zed.addRole("test",token -> {
            return token.equals("TEST");
        },role -> {
//            role.createEntityPermission(User.class);
        });
    }

//    @Bean
//    public Zed zed(){
//        Zed zed = new Zed();
//        zed.init();
//        return zed;
//    }
}
