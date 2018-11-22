//package com.beeasy.hzback.core.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.service.ApiKey;
//import springfox.documentation.service.AuthorizationScope;
//import springfox.documentation.service.SecurityReference;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spi.service.contexts.SecurityContext;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//import java.util.List;
//
////@Configuration
////@EnableSwagger2
//public class Swagger2Config {
//    @Bean
//    public Docket createRestApi() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.beeasy.hzback"))
//                .paths(PathSelectors.any())
//                .build()
//                .securitySchemes(ImmutableList.of(new ApiKey("Authorization", "Authorization", "header")))
//                .securityContexts(securityContexts());
//    }
//
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .title("springboot利用swagger构建api文档")
//                .description("简单优雅的restfun风格，http://blog.csdn.net/saytime")
//                .termsOfServiceUrl("http://blog.csdn.net/saytime")
//                .version("1.0")
//                .build();
//    }
//
//
//    private List<SecurityContext> securityContexts() {
//        return ImmutableList.of(
//                SecurityContext.builder()
//                        .securityReferences(defaultAuth())
//                        .build()
//        );
//    }
//
//    List<SecurityReference> defaultAuth() {
//        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
//        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//        authorizationScopes[0] = authorizationScope;
//        return ImmutableList.of(
//                new SecurityReference("Authorization", authorizationScopes));
//    }
//}
