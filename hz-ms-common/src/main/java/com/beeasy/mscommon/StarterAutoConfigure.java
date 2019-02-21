package com.beeasy.mscommon;

import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.File;

//@ImportResource(value = {"classpath:beetlsql.xml"})
@EnableFeignClients(value = {"com.beeasy.rpc"})
@Configuration
public class StarterAutoConfigure {

    @Autowired
    Environment environment;

//    @Bean(name = "dataSource")
//    public DataSource datasource(Environment env) {
//        HikariDataSource ds = new HikariDataSource();
//        ds.setJdbcUrl(env.getProperty("spring.datasource.url"));
//        ds.setUsername(env.getProperty("spring.datasource.username"));
//        ds.setPassword(env.getProperty("spring.datasource.password"));
//        ds.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
//        return ds;
//    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        String location = environment.getProperty("uploads.path");
        if(S.empty(location)){
            return factory.createMultipartConfig();
        }
        File tmpFile = new File(location, "temp");
        if (!tmpFile.exists()) {
            tmpFile.mkdirs();
        }
        factory.setLocation(tmpFile.getAbsolutePath());
        return factory.createMultipartConfig();
    }

    @Bean
    public ServletContextInitializer servletContextInitializer() {
        return new ServletContextInitializer() {

            @Override
            public void onStartup(ServletContext servletContext) throws ServletException {
                servletContext.getSessionCookieConfig().setName("HZSESSIONID");
            }
        };

    }

}
