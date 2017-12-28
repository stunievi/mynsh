//package com.beeasy.hzadmin.config;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import com.alibaba.druid.pool.DruidDataSourceFactory;
//
//import lombok.Getter;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.apache.ibatis.session.SqlSessionFactoryBuilder;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//import org.springframework.core.env.PropertiesPropertySource;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//
//import javax.sql.DataSource;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//
//@Configuration
//@EnableAutoConfiguration
//@MapperScan(basePackages = "com.beeasy.hzadmin.mapper")
//public class MybatisConfig {
////    @Autowired
////    private Environment env;
////
////
////    @Bean(name = "dataSource")
////    public DataSource dateSource() throws Exception {
////        Properties props = new Properties();
////        props.put("driverClassName",env.getProperty("db1.jdbc.driverClassName"));
////        props.put("url",env.getProperty("db1.jdbc.url"));
////        props.put("username",env.getProperty("db1.jdbc.username"));
////        props.put("password",env.getProperty("db1.jdbc.password"));
////
////        return DruidDataSourceFactory.createDataSource(props);
////    }
////
////    @Bean
////    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource ds) throws Exception {
////        SqlSessionFactoryBean fb = new SqlSessionFactoryBean();
////        fb.setDataSource(ds);
////        fb.setTypeAliasesPackage("com.beeasy.hzadmin");
////        fb.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mapping/*.xml"));
////        return fb.getObject();
////    }
//
//
//
//}
