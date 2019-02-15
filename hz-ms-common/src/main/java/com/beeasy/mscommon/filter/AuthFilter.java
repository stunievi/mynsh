package com.beeasy.mscommon.filter;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.mscommon.RestException;
import com.beeasy.mscommon.util.U;
import org.beetl.sql.core.SQLReady;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Order(1)
@Component
public class AuthFilter implements Filter {

    @Value("${auth.url}")
    String[] authUrl;
    @Value("${auth.not}")
    String[] notAuthUrl;



    public static final String Token = "HZToken";
    public static final String Uid = "HZUid";
    public static final String Uname = "HZUname";
    public static final String Utname = "HZUtname";
    public static final String Server = "HZServer";


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession();
        for (String nau : notAuthUrl) {
            if (request.getServletPath().startsWith(nau)) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
        }
        checkAuth:
        {
            for (String au : authUrl) {
                if (request.getServletPath().startsWith(au)) {
                    break checkAuth;
                }
            }
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        final String err = "请检查是否登录";
        String token = null;
        check:
        {
            Cookie[] cookies = request.getCookies();
            if (null != cookies) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equalsIgnoreCase("Authorization") || cookie.getName().equalsIgnoreCase(Token)) {
                        token = cookie.getValue();
                        break check;
                    }
                }
            }

            String[] keys = {"Authorization", "Token"};
            for (String key : keys) {
                //header
                token = request.getHeader(key);
                if (S.notBlank(token)) {
                    break;
                }
                //param
                token = request.getParameter(key);
                if (S.notBlank(token)) {
                    break;
                }
            }

            if (S.blank(token)) {
                throw new RestException(err);
            }
        }

        go_next_chain:
        {
            //if this token has already pass the authrization
            String server = (String) session.getAttribute(Server);
            String existToken = (String) request.getSession().getAttribute(Token);
            if (null != existToken && S.eq(existToken, token)) {
                break go_next_chain;
            }

//            SQLManager sqlManager = U.getSQLManager(server);

            //auth the token
            String finalToken = token;
            long uid;
            if (S.isIntOrLong(token)) {
                uid = Long.valueOf(token);
            } else {
                List<JSONObject> tokens = U.getSqliteSqlManager().execute(new SQLReady(S.fmt("select * from t_user_token where token = '%s'", token)), JSONObject.class);
                if (0 == tokens.size()) {
                    throw new RestException(err);
                }
                uid = tokens.get(0).getLong("userId");
                String[] arr = tokens.get(0).getString("type").split("\\|");
                if(arr.length == 2){
                    server = arr[1];
                }
                //兼容之前的单机
                else{
                    server = "main";
                }
            }
            if (0 == uid) {
                throw new RestException(err);
            }

            List<JSONObject> users = U.getSQLManager().execute(new SQLReady(S.fmt("select username,true_name from t_user where id = '%d'", uid)), JSONObject.class);
            if (0 == users.size()) {
                throw new RestException(err);
            }

            session.setAttribute(Token, token);
            session.setAttribute(Uid, uid);
            session.setAttribute(Uname, users.get(0).getString("username"));
            session.setAttribute(Utname, users.get(0).getString("trueName"));
            session.setAttribute(Server, server);

        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

    public static Long getUid() {
        return (Long) U.getRequest().getSession().getAttribute(AuthFilter.Uid);
    }

    public static String getUname() {
        return (String) U.getRequest().getSession().getAttribute(AuthFilter.Uname);
    }

    public static String getUtname() {
        return (String) U.getRequest().getSession().getAttribute(AuthFilter.Utname);
    }


}


