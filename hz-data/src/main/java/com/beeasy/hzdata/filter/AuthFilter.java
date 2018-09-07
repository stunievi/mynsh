package com.beeasy.hzdata.filter;

import com.beeasy.hzdata.entity.UserToken;
import com.beeasy.mscommon.Result;
import org.beetl.sql.core.SQLManager;
import org.osgl.$;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Component
public class AuthFilter implements Filter {

    @Autowired
    SQLManager sqlManager;

    private void writeError(ServletResponse response, final String message) throws IOException {
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setStatus(200);
        resp.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        resp.getWriter().write(Result.error(message).toJson());
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        final String err = "权限校验失败";
        String token = null;
        String[] keys = {"Authorization","Token"};
        for (String key : keys) {
            //header
            token = request.getHeader(key);
            if(S.notBlank(token)){
                break;
            }
            //param
            token = request.getParameter(key);
        }
        if(S.blank(token)){
            writeError(servletResponse,err);
            return;
        }

        String finalToken = token;
        long uid;
        if(S.isIntOrLong(token)){
            uid = Long.valueOf(token);
        }
        else {
            List<UserToken> list = sqlManager.lambdaQuery(UserToken.class)
                    .andEq(UserToken::getToken, token)
                    .andGreat(UserToken::getExprTime, new Date())
                    .select();
            if (list.size() == 0) {
                writeError(servletResponse,err);
                return;
            }
            uid = list.get(0).getUserId();
        }
        if (0 == uid) {
            writeError(servletResponse,err);
            return;
        }
        request.getSession().putValue("uid", uid);
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
