package com.beeasy.hzback.core.security;

import com.beeasy.hzback.modules.system.dao.IUserAllowApiDao;
import com.beeasy.hzback.modules.system.dao.IUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//    @Autowired
//    private MyFilterSecurityInterceptor myFilterSecurityInterceptor;

    @Autowired
    IUserDao userDao;
    @Autowired
    IUserAllowApiDao allowApiDao;

    @Bean
    CustomUserService customUserService(){ //注册UserDetailsService 的bean
        return new CustomUserService();
    }

    @Bean
    public Md5PasswordEncoder passwordEncoder(){
        return new Md5PasswordEncoder();
    }

    @Bean
    public JwtTokenUtil jwtTokenUtil(){
        return new JwtTokenUtil();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.authenticationProvider(new CustomAuthenticationProvider(userDetailsService,passwordEncoder()));
        auth.userDetailsService(customUserService())
            .passwordEncoder(passwordEncoder()); //user Details Service验证

    }





    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.headers().frameOptions().disable();

        http.authorizeRequests()

//                .antMatchers("/v2/api-docs").permitAll()
//                .antMatchers("/static/**").permitAll()
                .antMatchers("/crossdomain.xml").permitAll()
                .antMatchers("/api/login").permitAll()
                .antMatchers("/api/mobile/login").permitAll()
                .antMatchers("/api/mobile/login/**").permitAll()
                //错误页
//                .antMatchers("/error").permitAll()
                //头像
                .antMatchers("/api/mobile/download/face/**").permitAll()

                //封掉私有云登录
//                .antMatchers("/open/filecloud/apiLogin.action").denyAll()
//                .antMatchers("/templates/**").permitAll()
//                .antMatchers("/js/**").permitAll()

                //所有开放的API放到这里
                .antMatchers("/open/**").permitAll()
//                .antMatchers("/swagger-ui.html").permitAll()
//                .antMatchers("/swagger-resources/**").permitAll()
//                .antMatchers("/webjars/**").permitAll()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers("/api/**").authenticated()
//                .anyRequest().authenticated() //任何请求,登录后可以访问
                .and()
                .csrf().disable()
//                .cors().disable()
                .addFilter(new JWTAuthenticationFilter(authenticationManager(),jwtTokenUtil(),customUserService(), userDao, allowApiDao));

//        .addFilterBefore(new JWTAuthenticationFilter(authenticationManager(),jwtTokenUtil(),customUserService(), userDao), UsernamePasswordAuthenticationFilter.class);



    }



}
