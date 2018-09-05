//package com.beeasy.hzdata.module;
//
//import com.alibaba.dubbo.common.utils.DubboAppender;
//import com.beeasy.hzdata.utils.ClassUtil;
////import net.sf.cglib.proxy.Enhancer;
////import net.sf.cglib.proxy.MethodInterceptor;
////import net.sf.cglib.proxy.MethodProxy;
//import org.osgl.inject.Module;
//import org.springframework.context.ApplicationEvent;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.Method;
//import java.lang.reflect.Proxy;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class SpringModule extends Module {
//
//    public static ClassPathXmlApplicationContext ctx;
//
//    @Override
//    protected void configure() {
//        String packageName = "com.beeasy.rpc";
//        ctx = new ClassPathXmlApplicationContext(new String[]{"classpath:provider.xml"});
//        ctx.start();
//
//        Map<String,String> map = new HashMap<>();
//        for (String beanDefinitionName : ctx.getBeanDefinitionNames()) {
//            map.put(ctx.getType(beanDefinitionName).getName(), beanDefinitionName);
//        }
//
//        //扫描类
//        List<Class> classList = ClassUtil.getAllClass(packageName);
//        for (Class clazz : classList) {
//            if(!map.containsKey(clazz.getName())) {
//                continue;
//            }
//            registerGenericTypedBeanLoader(clazz, spec -> {
//                Object bean = ctx.getBean(map.get(clazz.getName()));
//                if (clazz.isInterface()) {
//                    Object dao = Proxy.newProxyInstance(getClass().getClassLoader(),
//                            new Class<?>[]{clazz},
//                            new Handler(bean)
//                    );
//                    return dao;
//                } else {
//                    return null;
////                    Enhancer enhancer = new Enhancer();
////                    enhancer.setClassLoader(getClass().getClassLoader());
////                    enhancer.setSuperclass(clazz);
////                    enhancer.setCallback(new Handler(bean));
////                    enhancer.setUseCache(true);
////                    return enhancer.create();
//                }
//            });
//        }
//    }
//
//    public static class Handler implements InvocationHandler{
//
//        private Object $instance = null;
//
//        public Handler(Object $instance) {
//            this.$instance = $instance;
//        }
//
//        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//            //取回原本的方法
//            Method m = $instance.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
//            return m.invoke($instance,args);
//        }
//
////        public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
////            //取回原本的方法
////            Method m = $instance.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
////            return m.invoke($instance,args);
////        }
//
//    }
//}
