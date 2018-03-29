package com.beeasy.hzback.core.security;
import com.alibaba.fastjson.JSON;
import com.beeasy.hzback.core.helper.Result;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collection;


@Slf4j
public class JWTAuthenticationFilter extends BasicAuthenticationFilter {


    private JwtTokenUtil jwtTokenUtil;
    private CustomUserService customUserService;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager,JwtTokenUtil jwtTokenUtil, CustomUserService customUserService) {
        super(authenticationManager);
        this.jwtTokenUtil = jwtTokenUtil;
        this.customUserService = customUserService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader("Authorization");

        if(!StringUtils.isEmpty(header)){
            UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
            if(authentication != null){
                SecurityContextHolder.getContext().setAuthentication(authentication);
                chain.doFilter(request, response);
            }
            else{
                response.setStatus(200);
                response.setHeader("content-type", "application/json;charset=UTF-8");
                Result result = Result.error("权限验证失败");
                response.getWriter().write(JSON.toJSONString(result));
            }
            return;
        }
        chain.doFilter(request, response);

    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null) {
            String userName = jwtTokenUtil.getUsernameFromToken(token);
            if(userName != null){
                SecurityUser su = (SecurityUser) customUserService.loadUserByUsername(userName);
                if(su != null){
                    return new UsernamePasswordAuthenticationToken(su.getUser(), su.getPassword(), su.getAuthorities());
                }
            }
        }
        return null;
    }


    @Override
    protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(200);
        response.getWriter().write("fuck");
//        super.onUnsuccessfulAuthentication(request, response, failed);
    }

    @Override
    protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException {
        super.onSuccessfulAuthentication(request, response, authResult);
    }
}