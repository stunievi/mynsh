package com.beeasy.zed;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.CharsetUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DB2SqlStyle;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DBService {
    public static SQLManager sqlManager;
    public static DataSource dataSource;
    public static JSONObject config;


    private static void initConfig() {
        String content;
        File file = new File("./config.json");
        try (
            FileReader reader = new FileReader(file);
            ){
            content = IoUtil.read(reader);
        } catch (IOException e) {
            ClassPathResource resource = new ClassPathResource("config.json");
            content = IoUtil.read(resource.getStream(), CharsetUtil.UTF_8);
        }
        config = JSON.parseObject(content);
    }

    public static void init(boolean dev) {
        initConfig();
        JSONObject ds = config.getJSONObject("datasource");
        ConnectionSource source;
        if(dev){
            //实例化类
            HikariConfig hikariConfig = new HikariConfig();
            //设置url
            hikariConfig.setJdbcUrl(ds.getString("url"));
            //数据库帐号
            hikariConfig.setUsername(ds.getString("username"));
            //数据库密码
            hikariConfig.setPassword(ds.getString("password"));
            hikariConfig.setDriverClassName(ds.getString("driver"));
            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
//            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
//            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(hikariConfig);
            source =  ConnectionSourceHelper.getSingle(dataSource);
        } else {
            DruidDataSource druidDataSource = new DruidDataSource();
            druidDataSource.setDriverClassName(ds.getString("driver"));
            druidDataSource.setUrl(ds.getString("url"));
            druidDataSource.setUsername(ds.getString("username"));
            druidDataSource.setPassword(ds.getString("password"));
            druidDataSource.setAsyncInit(true);
            dataSource = druidDataSource;
            source = ConnectionSourceHelper.getSingle(druidDataSource);
        }
        SQLLoader loader = new ClasspathLoader("/sql");
        UnderlinedNameConversion nc = new  UnderlinedNameConversion();
        sqlManager = new SQLManager(new DB2SqlStyle(),loader,source,nc,new Interceptor[]{new MyDebugInterceptor()});
    }
}
