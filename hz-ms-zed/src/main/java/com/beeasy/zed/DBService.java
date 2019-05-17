package com.beeasy.zed;

import com.alibaba.druid.pool.DruidDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DB2SqlStyle;

import javax.sql.DataSource;

import static com.beeasy.zed.Config.config;

public class DBService extends AbstractService{
    public static SQLManager sqlManager;
    public static DataSource dataSource;

    private static DBService instance;
    static {
        instance = new DBService();
    }

    private DBService(){
    }

    public static DBService getInstance(){
        return instance;
    }

    @Override
    public void initSync() {
        ConnectionSource source;
        if(config.dev){
            //实例化类
            HikariConfig hikariConfig = new HikariConfig();
            //设置url
            hikariConfig.setJdbcUrl(config.datasource.url);
            //数据库帐号
            hikariConfig.setUsername(config.datasource.username);
            //数据库密码
            hikariConfig.setPassword(config.datasource.driver);
            hikariConfig.setDriverClassName(config.datasource.password);
            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
//            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
//            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(hikariConfig);
            source =  ConnectionSourceHelper.getSingle(dataSource);
        } else {
            DruidDataSource druidDataSource = new DruidDataSource();
            druidDataSource.setDriverClassName(config.datasource.driver);
            druidDataSource.setUrl(config.datasource.url);
            druidDataSource.setUsername(config.datasource.username);
            druidDataSource.setPassword(config.datasource.password);
            druidDataSource.setAsyncInit(true);
            dataSource = druidDataSource;
            source = ConnectionSourceHelper.getSingle(druidDataSource);
        }
        SQLLoader loader = new ClasspathLoader("/sql");
        UnderlinedNameConversion nc = new  UnderlinedNameConversion();
        sqlManager = new SQLManager(new DB2SqlStyle(),loader,source,nc,new Interceptor[]{new MyDebugInterceptor()});
    }
}
