//package com.beeasy.hzback.core.config;
//
//import com.alibaba.druid.support.http.StatViewServlet;
//import com.alibaba.druid.support.http.WebStatFilter;
//import org.springframework.context.annotation.Configuration;
//
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.annotation.WebInitParam;
//import javax.servlet.annotation.WebServlet;
//
////
//@Configuration
//public class DruidConfig {
////    @Lazy
////    @Bean
////    @ConfigurationProperties(prefix = "spring.datasource")
////    public DataSource druidDataSource() {
////        DriverManagerDataSource druidDataSource = new DriverManagerDataSource();
////        return druidDataSource;
////    }
//
//    @SuppressWarnings("serial")
//    @WebServlet(urlPatterns = "/druid/*",
//            initParams = {
//                    @WebInitParam(name = "loginUsername", value = "test"),// 用户名
//                    @WebInitParam(name = "loginPassword", value = "test"),// 密码
//                    @WebInitParam(name = "resetEnable", value = "false")// 禁用HTML页面上的“Reset All”功能
//            })
//    public static class DruidStatViewServlet extends StatViewServlet {
//    }
//
//
//    /**
//     * @Package: pterosaur.account.config.filter
//     * @Description: 拦截druid监控请求
//     * @author: liuxin
//     * @date: 17/4/21 上午11:24
//     */
//    @WebFilter(filterName = "druidWebStatFilter", urlPatterns = "/*",
//            initParams = {
//                    @WebInitParam(name = "exclusions", value = "*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*")// 忽略资源
//            })
//    public static class DruidStatFilter extends WebStatFilter {
//    }
//}
