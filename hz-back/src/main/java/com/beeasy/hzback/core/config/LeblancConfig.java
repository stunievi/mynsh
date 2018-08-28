package com.beeasy.hzback.core.config;


import com.beeasy.common.helper.SpringContextUtils;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.core.util.SqlUtils;
import com.beeasy.hzback.modules.system.cache.SystemConfigCache;
import com.beeasy.hzback.modules.system.dao.IWorkflowModelDao;
import com.beeasy.common.entity.WorkflowModel;
import com.beeasy.hzback.modules.system.service.WorkflowService;
import org.apache.commons.io.IOUtils;
//import org.beetl.sql.core.SQLManager;
//import org.beetl.sql.core.SQLManagerBuilder;
//import org.beetl.sql.core.db.DB2SqlStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.script.*;
import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Configuration
public class LeblancConfig {

//    @Bean
//    public DataSetFactory dataSetFactory(){
//        return new DataSetFactory();
//    }
//
//    @Bean
//    public Zed zed(){
//        return new Zed();
//    }
//
//    @Bean
//    public SQLUtil sqlUtil(){
//        return new SQLUtil();
//    }
//
//    @Bean
//    public JPAUtil jpaUtil(){
//        return new JPAUtil();
//    }


    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    SystemConfigCache cache;

    @Autowired
    EntityManager entityManager;
//    @Autowired
//    SQLManager sqlManager;

    @Bean
    public ScriptEngine scriptEngine() {
        return new ScriptEngineManager().getEngineByName("javascript");
    }

    @Bean
    public ScriptContext scriptContext() {
        return new SimpleScriptContext();
    }

    @Bean
    public ApplicationStartListener applicationStartListener() {
        return new ApplicationStartListener();
    }

    public static boolean inited = false;

    @Transactional
    public class ApplicationStartListener implements ApplicationListener<ContextRefreshedEvent> {

        @Override
        public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
            if (inited) {
                return;
            }
            inited = true;

            //初始化脚本引擎
            ScriptEngine engine = scriptEngine();

            try {
                Bindings bindings = engine.getBindings(ScriptContext.GLOBAL_SCOPE);
                ClassPathResource resource = new ClassPathResource("config/behavior.js");
                List<String> codes = IOUtils.readLines(resource.getInputStream());
                engine.eval(String.join("\n", codes), bindings);
//            engine.eval(new FileReader(resource.getBytes()),bindings);
            } catch (ScriptException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (String sql : cache.getSqlViews()) {
                entityManager.createNativeQuery(sql).executeUpdate();
            }
        }

    }


//    @Service
//    @Transactional
//    public static class ViewService{
//        @Autowired
//        EntityManager entityManager;
//
//        @Async
//        public void createView(String sql){
//        }
//    }


}
