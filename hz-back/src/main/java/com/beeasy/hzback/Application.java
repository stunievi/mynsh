package com.beeasy.hzback;

//import com.spring4all.swagger.EnableSwagger2Doc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;


//@EnableSwagger2Doc
//@EnableWebMvc
//@EnableEurekaClient
//@Import(FdfsClientConfig.class)
@EnableAsync
@EnableScheduling
@EnableFeignClients(value = {"com.beeasy.hzback"})
//@EnableCaching
@SpringBootApplication(scanBasePackages = {"com.beeasy"})
@EntityScan(basePackages = {    "com.beeasy"})
//@ImportResource(value = {"classpath:provider.xml"})
//@ServletComponentScan
public class Application {


//    @Bean
//    public static PropertySourcesPlaceholderConfigurer properties() throws IOException {
//        Resource resource = new ClassPathResource("");
//        File file = new ClassPathResource("application.properties").getFile();
//        String appName = "";
//        while(true){
//            if(false) break;
//            File pfile = file.getParentFile();
//            if(null == pfile){
//                break;
//            }
//            if("WEB-INF".equals(file.getName())){
//                appName = pfile.getName();
//                break;
//            }
//            file = pfile;
//        }
//        if(S.empty(appName)){
//            appName = "hzback";
//        }
//        //尝试读取环境变量
//        String env = System.getenv(appName+"_config");
//        resource = new FileSystemResource(System.getProperty(env));
//        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
//        configurer.setIgnoreResourceNotFound(true);
//        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
//        yaml.setResources(resource);
//        configurer.setProperties(yaml.getObject());
//        return configurer;
//    }


//
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//        return builder.sources(Application.class);
//    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


//    @Bean
//    public EmbeddedServletContainerFactory servletContainer() {
//        JettyEmbeddedServletContainerFactory factory =
//                new JettyEmbeddedServletContainerFactory();
//        return factory;
//    }

//    @Bean
//    UndertowEmbeddedServletContainerFactory embeddedServletContainerFactory() {
//
//        UndertowEmbeddedServletContainerFactory factory = new UndertowEmbeddedServletContainerFactory();
//        factory.addBuilderCustomizers(builder -> {
//            builder.addHttpListener(8080,"0.0.0.0");
////            factory.add
//        });
//        // 这里也可以做其他配置
////        factory.addBuilderCustomizers(builder -> builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true));
//
//        return factory;
//    }

}