package com.beeasy.hzback.core.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//    @Autowired
//    private MyFilterSecurityInterceptor myFilterSecurityInterceptor;


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
                .antMatchers("/v2/api-docs").permitAll()
                .antMatchers("/static/**").permitAll()
                .antMatchers("/api/login").permitAll()
                .antMatchers("/api/mobile/login").permitAll()
                //所有开放的API放到这里
                .antMatchers("/open/**").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .anyRequest().authenticated() //任何请求,登录后可以访问
                .and()
                .csrf().disable()
//                .cors().disable()
                .addFilterBefore(new JWTAuthenticationFilter(authenticationManager(),jwtTokenUtil(),customUserService()), UsernamePasswordAuthenticationFilter.class);



    }



}
