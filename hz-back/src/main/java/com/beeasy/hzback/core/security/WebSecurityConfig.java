package com.beeasy.hzback.core.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//    @Autowired
//    private MyFilterSecurityInterceptor myFilterSecurityInterceptor;

    @Autowired
    UserDetailsService  userDetailsService;

    public final static String SIGNKEY = "MyJwtSecret";


    @Bean
    UserDetailsService customUserService(){ //注册UserDetailsService 的bean
        return new CustomUserService();
    }

    @Bean
    public Md5PasswordEncoder passwordEncoder(){
        return new Md5PasswordEncoder();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new CustomAuthenticationProvider(userDetailsService,passwordEncoder()));
//        auth.userDetailsService(customUserService())
//            .passwordEncoder(passwordEncoder()); //user Details Service验证

    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
                .antMatchers("/static/**").permitAll()
                .antMatchers("/api/login").permitAll()
                .antMatchers("/admin/**").authenticated()

//                .anyRequest().permitAll()
//                .anyRequest().authenticated() //任何请求,登录后可以访问
//                .and()
//                .openidLogin()
//                .loginPage("/api/login")
//                .defaultSuccessUrl("/admin")
//                .failureUrl("/login?error")
//                .permitAll() //登录页面用户任意访问
                .and()
                .logout()
                .permitAll() //注销行为任意访问
                .and()
                .csrf().disable();

        http.addFilter(new JWTLoginFilter(authenticationManager()));
        http.addFilter(new JWTAuthenticationFilter(authenticationManager()));
        /**
         * 如果跨域失败，尝试开启自定义filter
         */
    }



}
