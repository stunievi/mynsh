package com.beeasy.mscommon;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.util.U;
import com.ibm.db2.jcc.DB2Driver;
import javafx.application.Platform;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DB2SqlStyle;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.SQLiteStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.ext.spring4.SqlManagerFactoryBean;
import org.osgl.util.IO;
import org.osgl.util.S;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurationSelector;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.sqlite.JDBC;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Configuration
@EnableTransactionManagement
public class DataSourceConfiguration implements TransactionManagementConfigurer {




    @Bean(name = "dataSources")
    public Map<Object,Object> dataSources(){
        return new HashMap<>();
    }

    @Bean(name = "txManagers")
    public Map<String, DataSourceTransactionManager> txManagers(){
        return new HashMap<>();
    }

    @Bean(name = "sqlManagers")
    public Map<String, SQLManager> sqlManagers(){
        return new HashMap<>();
    }

    @Bean(name = "sqliteDataSource")
    @Primary
    public DataSource sqliteDataSource(){
        return DataSourceBuilder.create()
                .type(DruidDataSource.class)
                .driverClassName(org.sqlite.JDBC.class.getName())
                .url("jdbc:sqlite::resource:server/data.db")
                .build();
    }

    @Bean
    @Primary
    public SQLManager sqlManager(){
        return createSqlManager(new DB2SqlStyle(), routingDataSource());
    }


    public AbstractRoutingDataSource routingDataSource(){

        //sqlite数据源
        dataSources().put("@sqlite", sqliteDataSource());
        sqlManagers().put("@sqlite", createSqlManager(new SQLiteStyle(), sqliteDataSource()));
        txManagers().put("@sqlite", new DataSourceTransactionManager(sqliteDataSource()));

        ClassPathResource resource = new ClassPathResource("server/server.json");
        try {
            String str = IO.readContentAsString(resource.getInputStream());
            List<Server> list = JSON.parseObject(str, new TypeReference<List<Server>>(){});

            DataSource dft = null;
            for (Server server : list) {
                DataSource dataSource = DataSourceBuilder.create()
                        .type(DruidDataSource.class)
                        .driverClassName(server.driver)
                        .url(server.url)
                        .username(server.username)
                        .password(server.password)
                        .build();
                if(S.eq(server.name, "main")){
                    dft = dataSource;
                }
                dataSources().put(server.name, dataSource);
                sqlManagers().put(server.name, createSqlManager(new DB2SqlStyle(), dataSource));
                txManagers().put(server.name, new DataSourceTransactionManager(dataSource));
            }
            AbstractRoutingDataSource abstractRoutingDataSource = new AbstractRoutingDataSource() {
                @Override
                protected Object determineCurrentLookupKey() {
                    return Optional.ofNullable((ServletRequestAttributes)RequestContextHolder.getRequestAttributes())
                            .map(attr -> attr.getRequest())
                            .map(req -> req.getSession())
                            .map(session -> (String)session.getAttribute(AuthFilter.Server))
                            .filter(S::notBlank)
                            .orElse("main");
                }
            };
//            DataSourceContextHolder.setDataSourceType("main");
            abstractRoutingDataSource.setTargetDataSources(dataSources());
            abstractRoutingDataSource.setDefaultTargetDataSource(dft);
            abstractRoutingDataSource.afterPropertiesSet();
            return abstractRoutingDataSource;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager(){
        return new DataSourceTransactionManager(routingDataSource());
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return platformTransactionManager();
    }



    private static class Server{
        public String name;
        public String url;
        public String username;
        public String password;
        public String driver;
    }
    /**
     *
     * @param dbStyle
     * @param dataSource
     * @return
     */
    private SQLManager createSqlManager(DBStyle dbStyle, DataSource dataSource){
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        SQLLoader loader = new ClasspathLoader("/sql");
        UnderlinedNameConversion nc = new  UnderlinedNameConversion();
        return new SQLManager(dbStyle,loader,source,nc,new Interceptor[]{new DebugInterceptor()});
    }

}
