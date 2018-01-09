package com.beeasy.hzback.core.filter;

import com.beeasy.hzback.core.shiro.StatelessToken;
import com.beeasy.hzback.core.shiro.TokenManager;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class StatelessAuthcFilter extends AccessControlFilter {

    @Autowired
    private TokenManager tokenManager;

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String authorization = httpRequest.getHeader("authorization");
        Cookie[] a = httpRequest.getCookies();
        System.out.println(a);
//        if(StringUtils.isEmpty(authorization)){
//            onLoginFail(servletResponse,"fuck");
//            return false;
//        }
//        StatelessToken accessToken = tokenManager.getToken(authorization);
//        try {
//            getSubject(servletRequest,servletResponse).login(accessToken);
//        }
//        catch (Exception e){
//            onLoginFail(servletResponse,"fuck" + e.getMessage());
//            return false;
//        }
        getSubject(servletRequest, servletResponse).isPermitted(httpRequest.getRequestURI());
        return true;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        return false;
    }

    private void onLoginFail(ServletResponse response,String errorMsg) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpResponse.sendRedirect("http://www.baidu.com");

        httpResponse.setContentType("text/html");
        httpResponse.setCharacterEncoding("utf-8");
        httpResponse.getWriter().write(errorMsg);
        httpResponse.getWriter().close();
    }

}
