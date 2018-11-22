package com.beeasy.hzback;

//import com.spring4all.swagger.EnableSwagger2Doc;

import com.github.tobato.fastdfs.FdfsClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;


//@EnableSwagger2Doc
//@EnableWebMvc
//@EnableEurekaClient
@Import(FdfsClientConfig.class)
@EnableAsync
@EnableScheduling
@EnableFeignClients(value = {"com.beeasy.hzback"})
//@EnableCaching
@SpringBootApplication(scanBasePackages = {"com.beeasy"})
@EntityScan(basePackages = {"com.beeasy"})
//@ImportResource(value = {"classpath:provider.xml"})
//@ServletComponentScan
public class Application extends SpringBootServletInitializer {


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }

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