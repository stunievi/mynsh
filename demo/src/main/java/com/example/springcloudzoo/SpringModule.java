package com.example.springcloudzoo;

import act.app.App;
import com.example.demo.ClassUtil;
import com.example.demo.HelloWorldApp;
import com.example.demo.SpContext;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.osgl.inject.Module;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import sun.tracing.ProbeSkeleton;
import sun.tracing.ProviderSkeleton;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpringModule extends Module {
    public static AnnotationConfigApplicationContext ctx;

    @Override
    public void configure() {
        String packageName = "com.example.springcloudzoo";
        ctx = (AnnotationConfigApplicationContext) (SpContext.ctx = new AnnotationConfigApplicationContext());
        ctx.scan(packageName);
        ctx.refresh();

        Map<String,String> map = new HashMap<>();
        for (String beanDefinitionName : ctx.getBeanDefinitionNames()) {
            map.put(ctx.getType(beanDefinitionName).getName(), beanDefinitionName);
        }

        //扫描类
        List<Class> classList = ClassUtil.getAllClass(packageName);
        for (Class clazz : classList) {
            if(!map.containsKey(clazz.getName())) {
               continue;
            }
            registerGenericTypedBeanLoader(clazz, spec -> {
                Object bean = ctx.getBean(map.get(clazz.getName()));
                if (clazz.isInterface()) {
                    Object dao = Proxy.newProxyInstance(getClass().getClassLoader(),
                            new Class<?>[]{clazz},
                            new Handler(bean)
                    );
                    return dao;
                } else {
                    Enhancer enhancer = new Enhancer();
                    enhancer.setClassLoader(getClass().getClassLoader());
                    enhancer.setSuperclass(clazz);
                    enhancer.setCallback(new Handler(bean));
                    enhancer.setUseCache(true);
                    return enhancer.create();
                }
            });
        }
    }




    public static class Handler implements InvocationHandler, MethodInterceptor {

        private Object $instance = null;

        public Handler(Object $instance) {
            this.$instance = $instance;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //取回原本的方法
            Method m = $instance.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
            return m.invoke($instance,args);
        }

        public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            //取回原本的方法
            Method m = $instance.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
            return m.invoke($instance,args);
        }

    }
}
