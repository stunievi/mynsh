package com.beeasy.hzback.core.config;

import bin.leblanc.dataset.DataSetFactory;
import bin.leblanc.zed.JPAUtil;
import bin.leblanc.zed.RolePermission;
import bin.leblanc.zed.SQLUtil;
import bin.leblanc.zed.Zed;
import bin.leblanc.zed.event.ZedInitializedEvent;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.cache.SystemConfigCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.script.*;
import java.io.FileNotFoundException;

@Configuration
public class LeblancConfig implements ApplicationListener<ContextRefreshedEvent> {

    @Bean
    public DataSetFactory dataSetFactory(){
        return new DataSetFactory();
    }

    @Bean
    public Zed zed(){
        return new Zed();
    }

    @Bean
    public SQLUtil sqlUtil(){
        return new SQLUtil();
    }

    @Bean
    public JPAUtil jpaUtil(){
        return new JPAUtil();
    }


    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    SystemConfigCache cache;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        Zed zed = zed();

        zed.init();

        //基础需要注册超级管理员权限
        zed.addRole(RolePermission.SUPERUSER,(role) -> {
            role.allowAllGet();
            role.allowAllPost();
            role.allowAllPut();
            role.allowAllDelete();
        });
        zed.addRole(RolePermission.UNKNOWN, role -> {
            role.disallowAllDelete();
            role.disallowAllGet();
            role.disallowAllPost();
            role.disallowAllPut();
        });




        applicationContext.publishEvent(new ZedInitializedEvent(this));

        //初始化脚本引擎
        ScriptEngine engine = scriptEngine();

        try {
            Bindings bindings = engine.getBindings(ScriptContext.GLOBAL_SCOPE);
            engine.eval(Utils.getReader("classpath:config/behavior.js"),bindings);
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }


    @Bean
    public ScriptEngine scriptEngine(){
        return new ScriptEngineManager().getEngineByName("javascript");
    }

    @Bean
    public ScriptContext scriptContext(){
        return new SimpleScriptContext();
    }

}
