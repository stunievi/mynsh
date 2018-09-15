package com.beeasy.mscommon;

import com.zaxxer.hikari.HikariDataSource;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.servlet.MultipartConfigElement;
import javax.sql.DataSource;
import java.io.File;

//@ImportResource(value = {"classpath:beetlsql.xml"})
@EnableFeignClients(value = {"com.beeasy.rpc"})
@Configuration
public class StarterAutoConfigure {

    @Autowired
    Environment environment;

    @Bean(name = "dataSource")
    public DataSource datasource(Environment env) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(env.getProperty("spring.datasource.url"));
        ds.setUsername(env.getProperty("spring.datasource.username"));
        ds.setPassword(env.getProperty("spring.datasource.password"));
        ds.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        return ds;
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        String location = environment.getProperty("uploads.temp");
        if(S.empty(location)){
            return factory.createMultipartConfig();
        }
        File tmpFile = new File(location);
        if (!tmpFile.exists()) {
            tmpFile.mkdirs();
        }
        factory.setLocation(location);
        return factory.createMultipartConfig();
    }

}
