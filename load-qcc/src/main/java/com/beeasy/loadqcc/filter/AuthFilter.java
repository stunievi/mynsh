package com.beeasy.loadqcc.filter;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.mscommon.Result;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Order(1)
@Component
public class AuthFilter implements Filter {

    @Value("${auth.url}")
    String[] authUrl;
    @Value("${auth.not}")
    String [] notAuthUrl;

    @Autowired
    SQLManager sqlManager;
    @Autowired
    Environment environment;
//    @Autowired
//    FJHttpMessageConverter httpMessageConverter;

    public static final String Token = "HZToken";
    public static final String Uid = "HZUid";
    public static final String Uname = "HZUname";
    public static final String Utname = "HZUtname";

    private void writeError(ServletResponse response, final String message) throws IOException {
//        String jsonpValue = httpMessageConverter.getJsonpParam();
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setStatus(200);
        resp.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        String str = Result.error(message).toJson();
//        if($.isNotNull(jsonpValue)){
//            str = S.fmt("%s(%s);",jsonpValue, str);
//            resp.setContentType(httpMessageConverter.APPLICATION_JAVASCRIPT.toString());
//        }
        resp.getWriter().write(str);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
            for (String nau : notAuthUrl) {
                if(request.getServletPath().startsWith(nau)){
                    filterChain.doFilter(servletRequest,servletResponse);
                    return;
                }
            }
            checkAuth: do{
                for (String au : authUrl) {
                    if(request.getServletPath().startsWith(au)){
                        break checkAuth;
                    }
                }
                filterChain.doFilter(servletRequest,servletResponse);
                return;
            }while(false);

        final String err = "请检查是否登录";
        String token = null;
        check: do{
            Cookie[] cookies = request.getCookies();
            if(null != cookies){
                for (Cookie cookie : cookies) {
                    if(cookie.getName().equalsIgnoreCase("Authorization") || cookie.getName().equalsIgnoreCase(Token)){
                        token = cookie.getValue();
                        break check;
                    }
                }
            }
            String[] keys = {"Authorization","Token"};
            for (String key : keys) {
                //header
                token = request.getHeader(key);
                if(S.notBlank(token)){
                    break;
                }
                //param
                token = request.getParameter(key);
                if(S.notBlank(token)){
                    break;
                }
            }
        }while (false);
        if(S.blank(token)){
            writeError(servletResponse,err);
            return;
        }

        //if this token has already pass the authrization
        String existToken = (String) request.getSession().getAttribute(Token);
        if(null != existToken && S.eq(existToken,token)){
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }

        //auth the token
        String finalToken = token;
        long uid;
        if(S.isIntOrLong(token)){
            uid = Long.valueOf(token);
        }
        else {
            List<JSONObject> tokens = sqlManager.execute(new SQLReady(S.fmt("select * from t_user_token where token = '%s'", token)), JSONObject.class);
            if(0 == tokens.size()){
                writeError(servletResponse, err);
            }
            uid = (long) tokens.get(0).get("USER_ID");
        }
        if (0 == uid) {
            writeError(servletResponse,err);
            return;
        }

        List<JSONObject> users = sqlManager.execute(new SQLReady(S.fmt("select username,true_name from t_user where id = '%d'", uid)), JSONObject.class);
        if(0 == users.size()){
            writeError(servletResponse, err);
            return;
        }
        request.getSession().setAttribute(Token, token);
        request.getSession().setAttribute(Uid, uid);
        request.getSession().setAttribute(Uname, users.get(0).getString("USERNAME"));
        request.getSession().setAttribute(Utname, users.get(0).getString("TRUE_NAME"));
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }

    public static Long getUid(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return (Long) request.getSession().getAttribute(AuthFilter.Uid);
    }

    public static String getUname(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return (String) getRequest().getSession().getAttribute(AuthFilter.Uname);
    }

    public static String getUtname(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return (String) getRequest().getSession().getAttribute(AuthFilter.Utname);
    }

    public static HttpServletRequest getRequest(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request;
    }

}


