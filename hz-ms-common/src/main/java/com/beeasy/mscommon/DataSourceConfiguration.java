package com.beeasy.mscommon;

import com.alibaba.druid.pool.DruidDataSource;
import com.beeasy.mscommon.util.U;
import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DB2SqlStyle;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.osgl.util.S;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@ConfigurationProperties(prefix = "multipul")  // 配置文件中的前缀
public class DataSourceConfiguration implements TransactionManagementConfigurer {

    @Getter
    @Setter
    List<Server> datasources;

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

//    @Bean(name = "sqliteDataSource")
//    @Primary
//    public DataSource sqliteDataSource(){
//        return routingDataSource();
//    }

    @Bean
    @Primary
    public SQLManager sqlManager(){
        return createSqlManager(new DB2SqlStyle(), routingDataSource());
    }


    @Bean(name = "dataSource")
    public AbstractRoutingDataSource routingDataSource(){

        //sqlite数据源
//        dataSources().put("@sqlite", sqliteDataSource());
//        sqlManagers().put("@sqlite", createSqlManager(new SQLiteStyle(), sqliteDataSource()));
//        txManagers().put("@sqlite", new DataSourceTransactionManager(sqliteDataSource()));


//        ClassPathResource resource = new ClassPathResource("server/server.json");
        try {
            DataSource dft = null;
            for (Server server : datasources) {
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
                //别名的处理
                if(S.notBlank(server.alias)){
                    dataSources().put(server.alias, dataSource);
                    sqlManagers().put(server.alias, createSqlManager(new DB2SqlStyle(), dataSource));
                    txManagers().put(server.alias, new DataSourceTransactionManager(dataSource));
                }
            }
            AbstractRoutingDataSource abstractRoutingDataSource = new AbstractRoutingDataSource() {
                @Override
                protected Object determineCurrentLookupKey() {
                    return U.getServer();
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



    @Getter
    @Setter
    public static class Server{
        public String name;
        public String url;
        public String username;
        public String password;
        public String driver;
        public String alias;
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
