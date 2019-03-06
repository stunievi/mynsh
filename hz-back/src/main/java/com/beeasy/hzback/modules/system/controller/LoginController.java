package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.entity.User;
import com.beeasy.hzback.entity.UserToken;
import com.beeasy.mscommon.RestException;
import com.beeasy.mscommon.Result;
import com.beeasy.mscommon.util.U;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.beetl.sql.core.SQLManager;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static com.beeasy.mscommon.filter.AuthFilter.*;


@Api(tags = "登录API", description = "登录接口")
@RestController
@RequestMapping("/api")
public class LoginController {


    @ApiOperation(value = "登录接口", notes = "根据用户名和密码换取JWT秘钥，连续3次登录失败会被锁定15分钟不可登录")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "username", value = "用户名", required = true),
        @ApiImplicitParam(name = "password", value = "密码", required = true)
    })
    @RequestMapping(value = "/login")
    public Result login(
        @RequestParam String username,
        @RequestParam String password,
        @RequestParam(required = false, defaultValue = "main") String server
        , HttpServletRequest request
        , HttpServletResponse response) {

        password = DigestUtils.md5DigestAsHex(password.getBytes());
//        QueryCondition<User> qc = new QueryCondition<>();
//        qc.orEq("username", username);
//        qc.orEq("acc_code", username);
        SQLManager sqlManager = U.getSQLManager(server);
        if (sqlManager == null) {
            throw new RestException("登录失败");
        }
        User user = sqlManager.selectSingle("user.用户登录校验", C.newMap("username", username, "password", password),User.class);
//        List<User> us = sqlManager.lambdaQuery(User.class)
//            .and(qc)
//            .andEq(User::getPassword, password)
//            .select(User::getId,User::getTrueName,User::getCloudUsername,User::getCloudPassword);

        if (null == user) {
            throw new RestException("登录失败,用户名或密码错误");
        }
//

        DataSourceTransactionManager manager = U.getTxManager(server);
        TransactionStatus transactionStatus = manager.getTransaction(new DefaultTransactionDefinition(){{setPropagationBehavior(PROPAGATION_REQUIRES_NEW);}});
        try{
            UserToken userToken = new UserToken();
            userToken.setToken(server + "|" + S.uuid());
            userToken.setUserId(user.getId());
            userToken.setExprTime(new Date(System.currentTimeMillis() + 30 * 60 * 1000));
            userToken.setType(UserToken.Type.WEB);
            sqlManager.insert(userToken, true);
            manager.commit(transactionStatus);

            //set cache
            request.getSession().setAttribute(Token, userToken.getToken());
            request.getSession().setAttribute(Uid, userToken.getUserId());
            request.getSession().setAttribute(Server, server);

            response.addCookie(new Cookie(Token, userToken.getToken()));
            return Result.ok(
                    C.newMap(
                            "token", userToken.getToken()
                            , "user", user
                    )
            );

        }
        catch (Exception e){
            e.printStackTrace();
            manager.rollback(transactionStatus);
            throw new RestException("登录失败");
        }

    }



}
