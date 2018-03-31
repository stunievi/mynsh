//package com.beeasy.hzback.lib.zed;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
//import org.springframework.beans.factory.config.BeanDefinitionHolder;
//import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.beans.factory.support.BeanDefinitionRegistry;
//import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
//import org.springframework.beans.factory.support.GenericBeanDefinition;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.core.type.filter.AnnotationTypeFilter;
//import org.springframework.stereotype.Component;
//
//import javax.persistence.Entity;
//import java.util.Map;
//import java.util.Set;
//
//@Slf4j
//@Component
//@Lazy(true)
//public class AnnotationScannerConfigurer implements BeanDefinitionRegistryPostProcessor {
//    @Override
//    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
//
//    }
//
//    @Override
//    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
////        configurableListableBeanFactory.
//        Map<String, Object> map=configurableListableBeanFactory.getBeansWithAnnotation(Entity.class);
//        log.info(map.size() + "");
//    }
//
//
//
//    /**
//     * 类扫描器，扫描给定包及其子包中的所有类
//     * @author abao
//     *
//     */
//
////    @Component
////    public static class BeanScannerConfigurer implements BeanFactoryPostProcessor, ApplicationContextAware {
////
////        private ApplicationContext applicationContext;
////
////        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
////            this.applicationContext = applicationContext;
////        }
////
////        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
////            Scanner scanner = new Scanner((BeanDefinitionRegistry) beanFactory);
////            scanner.setResourceLoader(this.applicationContext);
////            scanner.scan("org.wcong.test.spring.scan");
////        }
////    }
////
////    public final static class Scanner extends ClassPathBeanDefinitionScanner {
////
////        public Scanner(BeanDefinitionRegistry registry) {
////            super(registry);
////        }
////
////        public void registerDefaultFilters() {
////            this.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
////        }
////
////        public Set<BeanDefinitionHolder> doScan(String... basePackages) {
////            Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
////            for (BeanDefinitionHolder holder : beanDefinitions) {
////                GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
////                definition.getPropertyValues().add("innerClassName", definition.getBeanClassName());
////                definition.setBeanClass(Entity.class);
////            }
////            return beanDefinitions;
////        }
////
////        public boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
////            return super.isCandidateComponent(beanDefinition) && beanDefinition.getMetadata()
////                    .hasAnnotation(Entity.class.getNodeName());
////        }
////
////    }
//
////    public static class FactoryBeanTest<T> implements InitializingBean, FactoryBean<T> {
////
////        private String innerClassName;
////
////        public void setInnerClassName(String innerClassName) {
////            this.innerClassName = innerClassName;
////        }
////
////        public T getObject() throws Exception {
////            Class innerClass = Class.forName(innerClassName);
////            if (innerClass.isInterface()) {
////                return (T) InterfaceProxy.newInstance(innerClass);
////            } else {
////                Enhancer enhancer = new Enhancer();
////                enhancer.setSuperclass(innerClass);
////                enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
////                enhancer.setCallback(new MethodInterceptorImpl());
////                return (T) enhancer.create();
////            }
////        }
////
////        public Class<?> getObjectType() {
////            try {
////                return Class.forName(innerClassName);
////            } catch (ClassNotFoundException e) {
////                e.printStackTrace();
////            }
////            return null;
////        }
////
////        public boolean isSingleton() {
////            return true;
////        }
////
////        public void afterPropertiesSet() throws Exception {
////
////        }
////    }
//
////    public static class InterfaceProxy implements InvocationHandler {
////
////        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
////            System.out.println("ObjectProxy execute:" + method.getNodeName());
////            return method.invoke(proxy, args);
////        }
////
////        public static <T> T newInstance(Class<T> innerInterface) {
////            ClassLoader classLoader = innerInterface.getClassLoader();
////            Class[] interfaces = new Class[] { innerInterface };
////            InterfaceProxy proxy = new InterfaceProxy();
////            return (T) Proxy.newProxyInstance(classLoader, interfaces, proxy);
////        }
////    }
////
////    public static class MethodInterceptorImpl implements MethodInterceptor {
////
////        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
////            System.out.println("MethodInterceptorImpl:" + method.getNodeName());
////            return methodProxy.invokeSuper(o, objects);
////        }
////    }
//
//
//
//}
//
//
//
//
//
