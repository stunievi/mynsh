//package com.beeasy.hzreport.config;
//
//import com.beeasy.hzreport.utils.Utils;
//import org.beetl.sql.core.SQLManager;
//import org.beetl.sql.core.mapping.BeanProcessor;
//import org.osgl.util.C;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.event.ContextRefreshedEvent;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Configuration
//public class BeetlSqlConfig implements ApplicationListener<ContextRefreshedEvent> {
//
////    @Bean
////    public SQLManager getSqlManager(@Autowired DataSource dataSource){
////        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
////        DBStyle mysql = new DB2SqlStyle();
////// sql语句放在classpagth的/sql 目录下
////        SQLLoader loader = new ClasspathLoader("/sql");
////// 数据库命名跟java命名一样，所以采用DefaultNameConversion，还有一个是UnderlinedNameConversion，下划线风格的
////        UnderlinedNameConversion nc = new  UnderlinedNameConversion();
////// 最后，创建一个SQLManager,DebugInterceptor 不是必须的，但可以通过它查看sql执行情况
////        SQLManager sqlManager = new SQLManager(mysql,loader,source,nc,new Interceptor[]{new DebugInterceptor()});
////        return sqlManager;
////    }
//
//    @Autowired
//    SQLManager sqlManager;
//
//
//    //sqlid which will be keeped by underline stype
//    static List<String> sqlids = C.newList(
//            "system.selectConfigByKey",
//            "system.selectConfigs",
//            "task.selectGentask"
//    );
//    static {
//        for(int i = 1; i <= 16; i++){
//            sqlids.add("task.selectRule" + i);
//        }
//    }
//
//    @Override
//    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
//        BeanProcessor bp = new BeanProcessor(sqlManager){
//            @Override
//            public Map<String, Object> toMap(String sqlId, Class<?> c, ResultSet rs) throws SQLException {
//                Map<String,Object> map = super.toMap(sqlId, c, rs);
//                return Utils.underLineMap(map);
//            }
//        };
//        sqlManager.setProcessors(
//                sqlids.stream().collect(Collectors.toMap(sqlid -> sqlid, sqlid -> bp))
//        );
//
//
//    }
//}
//
