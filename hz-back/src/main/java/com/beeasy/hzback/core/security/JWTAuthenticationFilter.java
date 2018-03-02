package com.beeasy.hzback.core.security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collection;

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

        if (header == null) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

        SecurityContextHolder.getContext().setAuthentication(authentication);
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

}