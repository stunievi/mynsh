package com.beeasy.hzback;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableSwagger2Doc
//@EnableWebMvc
//@EnableAsync
@EnableScheduling
@EnableJpaAuditing
//@EnableCaching
@SpringBootApplication(scanBasePackages = {"com.beeasy.hzback"})
public class Application{

    public static void main(String[] args){
        SpringApplication.run(Application.class,args);
    }


//    @Bean
//    public EmbeddedServletContainerFactory servletContainer() {
//        JettyEmbeddedServletContainerFactory factory =
//                new JettyEmbeddedServletContainerFactory();
//        return factory;
//    }

    @Bean
    UndertowEmbeddedServletContainerFactory embeddedServletContainerFactory() {

        UndertowEmbeddedServletContainerFactory factory = new UndertowEmbeddedServletContainerFactory();
        factory.addBuilderCustomizers(builder -> {
            builder.addHttpListener(8080,"0.0.0.0");
//            factory.add
        });
        // 这里也可以做其他配置
//        factory.addBuilderCustomizers(builder -> builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true));

        return factory;
    }

}