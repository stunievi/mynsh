package com.beeasy.hzback.core.security;

import com.alibaba.fastjson.JSON;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.modules.system.dao.IUserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;


@Slf4j
public class JWTAuthenticationFilter extends BasicAuthenticationFilter {

    private JwtTokenUtil jwtTokenUtil;
    private CustomUserService customUserService;
    private IUserDao userDao;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, CustomUserService customUserService, IUserDao userDao) {
        super(authenticationManager);
        this.jwtTokenUtil = jwtTokenUtil;
        this.customUserService = customUserService;
        this.userDao = userDao;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = Optional.ofNullable(request.getHeader("Authorization")).orElseGet(() -> request.getParameter("Authorization"));
        do {
            if (StringUtils.isEmpty(header)) {
                break;
            }
            UsernamePasswordAuthenticationToken authentication = getAuthentication(header);

            if (authentication != null) {
//                User user = (User) authentication.getPrincipal();
//                String url = request.getServletPath();
                //得到用户的授权列表
                //没有的话暂时略过
//                Optional<RolePermission> rolePermission = user.getMethodPermission();
//                if (!rolePermission.isPresent()) {
//                    break;
//                }
//                if (rolePermission.get().getUnbindItems().contains(url)) {
//                    break;
//                }

                SecurityContextHolder.getContext().setAuthentication(authentication);
                break;
            }

            //授权失败
            response.setStatus(200);
            response.setHeader("content-type", "application/json;charset=UTF-8");
            Result result = Result.error("权限验证失败");
            response.getWriter().write(JSON.toJSONString(result));
        } while (false);
        chain.doFilter(request, response);

    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        Long uid = jwtTokenUtil.getUserIdFromToken(token);
        if(org.apache.commons.lang.math.NumberUtils.isNumber(token)){
            uid = Long.valueOf(token);
        }
        if (uid != null) {
            if(userDao.countById(uid) > 0){
                return new UsernamePasswordAuthenticationToken(uid, "", new ArrayList<>());
            }
        }
        return null;
    }


    @Override
    protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(200);
        response.getWriter().write("error");
//        super.onUnsuccessfulAuthentication(request, response, failed);
    }

    @Override
    protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException {
        super.onSuccessfulAuthentication(request, response, authResult);
    }
}