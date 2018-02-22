package com.beeasy.hzback.lib.zed.config;

import com.beeasy.hzback.lib.zed.Zed;
import com.beeasy.hzback.modules.setting.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.util.Arrays;

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

        zed.addRole("test2",token -> {
            return token.equals("TEST2");
        },role -> {
            role.createEntityPermission(User.class)
                    .allowGet()
                    .setGetReturnFields(new String[]{"id","username"});
        });

        zed.addRole("test3",token -> {
            return token.equals("TEST3");
        },role -> {
            role.createEntityPermission(User.class)
                    .allowGet()
                    .setUniqueWhereFields(new String[]{"username"});

        });

        zed.addRole("test4",token -> {
            return token.equals("TEST4");
        },role -> {
            role.createEntityPermission(User.class)
                    .allowGet()
                    .setWhereLimit((cb,root,condition) -> {
                        //限制ID只能在1和10之间取
                        Predicate c = root.get("id").in(Arrays.asList(new Integer[]{9}));
                        return c;
                    });
        });
    }


}
