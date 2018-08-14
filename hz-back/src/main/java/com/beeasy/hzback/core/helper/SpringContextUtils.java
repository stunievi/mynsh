//package com.beeasy.hzback.core.helper;
//
//import org.springframework.beans.BeansException;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.stereotype.Component;
//
//@Component
//public class SpringContextUtils implements ApplicationContextAware {
//    private static ApplicationContext context;
//
//    @Override
//    public void setApplicationContext(ApplicationContext context)
//            throws BeansException {
//        SpringContextUtils.context = context;
//    }
//    public static ApplicationContext getContext(){
//        return context;
//    }
//
//
//    public static <T> T getBean(Class<T> type){
//        return getContext().getBean(type);
//    }
//
//    public static Object getBeanObject(Class type){
//        return getContext().getBean(type);
//    }
//}