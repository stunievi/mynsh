package com.beeasy.hzback.core.config;


import cn.hutool.core.util.ClassUtil;
import com.beeasy.hzback.core.util.ClassUtils;
import com.beeasy.hzback.modules.system.cache.SystemConfigCache;
import com.beeasy.mscommon.valid.ValidGroup;
import org.apache.commons.io.IOUtils;
import org.beetl.sql.core.JavaType;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.kit.BeanKit;
import org.beetl.sql.core.mapping.BeanProcessor;
import org.beetl.sql.core.mapping.type.JavaSqlTypeHandler;
import org.beetl.sql.core.mapping.type.TypeParameter;
import org.beetl.sql.ext.SnowflakeIDWorker;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;

import javax.script.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

//import org.beetl.sql.core.SQLManager;
//import org.beetl.sql.core.SQLManagerBuilder;
//import org.beetl.sql.core.db.DB2SqlStyle;

@Configuration
public class LeblancConfig {

    public static Map<String,Class> ClassMap = C.newMap();

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
    SQLManager sqlManager;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    SystemConfigCache cache;

//    @Autowired
//    SQLManager sqlManager;

    @Bean
    public SnowflakeIDWorker snowflakeIDWorker(){
        return new SnowflakeIDWorker(1L,1L);
    }

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

            sqlManager.setDefaultBeanProcessors(new BeanProcessor(sqlManager){

//                private NameConversion ulnc = new UnderlinedNameConversion();

                @Override
                public Map<String, Object> toMap(String sqlId, Class<?> c, ResultSet rs) throws SQLException {
                    Map<String, Object> result = BeanKit.getMapIns(c);
                    boolean notConvert = sqlId.endsWith("UL") || sqlId.startsWith("accloan.");
                    if (c == null) {
                        throw new SQLException("不能映射成Map:" + c);
                    } else {
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int cols = rsmd.getColumnCount();
                        TypeParameter tp = new TypeParameter(sqlId, this.dbName, (Class)null, rs, rsmd, 0);

                        for(int i = 1; i <= cols; ++i) {
                            String columnName = rsmd.getColumnLabel(i);
                            if (null == columnName || 0 == columnName.length()) {
                                columnName = rsmd.getColumnName(i);
                            }

                            int colType = rsmd.getColumnType(i);
                            if (this.dbType != 2 && this.dbType != 4 || !columnName.equalsIgnoreCase("beetl_rn")) {
                                Class classType = (Class) JavaType.jdbcJavaTypes.get(colType);
                                JavaSqlTypeHandler handler = (JavaSqlTypeHandler)this.handlers.get(classType);
                                if (handler == null) {
                                    handler = this.defaultHandler;
                                }

                                tp.setIndex(i);
                                tp.setTarget(classType);
                                Object value = handler.getValue(tp);
                                if(notConvert){
                                    result.put(columnName.toUpperCase(), value);
                                }
                                else{
                                    result.put(this.nc.getPropertyName(c, columnName), value);
                                }
                            }
                        }

                        return result;
                    }
                }
            });
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
