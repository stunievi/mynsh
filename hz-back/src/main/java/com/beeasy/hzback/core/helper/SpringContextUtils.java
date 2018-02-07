package com.beeasy.hzback.core.helper;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtils<T> implements ApplicationContextAware {
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context)
            throws BeansException {
        SpringContextUtils.context = context;
    }
    public static ApplicationContext getContext(){
        return context;
    }


    public static Object getBean(Class<?> type){
        return getContext().getBean(type.getSimpleName());
    }
}