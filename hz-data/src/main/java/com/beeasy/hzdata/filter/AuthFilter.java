package com.beeasy.hzdata.filter;

import act.app.ActionContext;
import act.handler.builtin.controller.BeforeInterceptor;
import act.plugin.Plugin;
import act.security.CORS;
import com.alibaba.fastjson.JSON;
import com.beeasy.hzdata.entity.User;
import com.beeasy.hzdata.entity.UserToken;
import com.beeasy.hzdata.utils.RetJson;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.ext.spring4.SqlManagerFactoryBean;
import org.jetbrains.annotations.Contract;
import org.osgl.exception.FastRuntimeException;
import org.osgl.http.H;
import org.osgl.mvc.annotation.Before;
import org.osgl.mvc.result.*;
import org.osgl.util.S;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Constructor;
import java.util.*;

@Singleton
public class AuthFilter {

    @Before
    public Result before(H.Request request, SQLManager sqlManager, H.Flash flash){
        Result error = (RetJson.error("权限校验失败")).toResult();
        String token = null;
        String[] keys = {"Authorization","Token"};
        for (String key : keys) {
            //header
            token = request.header(key);
            if(null != token && !token.isEmpty()){
                break;
            }
            //param
            token = request.paramVal(key);
        }
        if(null == token || token.isEmpty()){
            return error;
        }

        String finalToken = token;
        long uid;
        if(S.isIntOrLong(token)){
            uid = Long.valueOf(token);
        }
        else {
            List<UserToken> list = sqlManager.query(UserToken.class)
                    .andEq("token", token)
                    .andGreat("expr_time", new Date())
                    .select();
            if (list.size() == 0) {
                return error;
            }
            uid = list.get(0).userId;
        }
        if (0 == uid) {
            return error;
        }
        flash.put("uid", uid + "");
        return null;
    }

}
