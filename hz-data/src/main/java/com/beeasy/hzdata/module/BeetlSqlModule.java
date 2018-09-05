//package com.beeasy.hzdata.module;
//
//import act.Act;
//import act.db.beetlsql.BeetlSqlService;
//import com.alibaba.dubbo.config.RegistryConfig;
//import com.alibaba.dubbo.config.ServiceConfig;
//import com.beeasy.rpc.DataService;
//import com.beeasy.rpc.DataServiceImpl;
//import org.beetl.sql.core.SQLManager;
//import org.osgl.inject.Module;
//
//public class BeetlSqlModule extends Module {
//
//    @Override
//    protected void configure() {
//        registerGenericTypedBeanLoader(SQLManager.class, spec -> {
//            BeetlSqlService beetlSqlService = Act.app().dbServiceManager().dbService("default");
//            return beetlSqlService.beetlSql();
//        });
//
//
//        ServiceConfig<DataService> service = new ServiceConfig<DataService>(); // 此实例很重，封装了与注册中心的连接，请自行缓存，否则可能造成内存和连接泄漏
////        service.setApplication(SpringModule.ctx.getParent());
//        RegistryConfig config = new RegistryConfig();
//        config.setAddress("redis://127.0.0.1:6379");
//        config.setTimeout(60000);
//        service.setRegistry(config);
//        service.setInterface(Act.appClassForName("com.beeasy.rpc.DataService"));
//        service.setRef(new DataServiceImpl());
//        service.export();
//    }
//}
