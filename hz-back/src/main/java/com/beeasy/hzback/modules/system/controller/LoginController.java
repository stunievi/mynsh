package com.beeasy.hzback.modules.system.controller;

import com.beeasy.mscommon.Result;
import com.beeasy.mscommon.RestException;
import com.beeasy.hzback.entity.User;
import com.beeasy.hzback.entity.UserToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.beetl.sql.core.SQLManager;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static com.beeasy.mscommon.filter.AuthFilter.Token;
import static com.beeasy.mscommon.filter.AuthFilter.Uid;


@Api(tags = "登录API", description = "登录接口")
@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    SQLManager sqlManager;


    @Transactional
    @ApiOperation(value = "登录接口", notes = "根据用户名和密码换取JWT秘钥，连续3次登录失败会被锁定15分钟不可登录")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "username", value = "用户名", required = true),
        @ApiImplicitParam(name = "password", value = "密码", required = true)
    })
    @RequestMapping(value = "/login")
    public Result login(
        @RequestParam String username,
        @RequestParam String password
        , HttpServletRequest request
        , HttpServletResponse response) {

        password = DigestUtils.md5DigestAsHex(password.getBytes());
        List<User> us = sqlManager.lambdaQuery(User.class)
            .andEq(User::getUsername, username)
            .andEq(User::getPassword, password)
            .select(User::getId,User::getTrueName,User::getCloudUsername,User::getCloudPassword);

        if (us.size() == 0) {
            throw new RestException("登录失败,用户名或密码错误");
        }

        User user = us.get(0);

        UserToken userToken = new UserToken();
        userToken.setToken(UUID.randomUUID().toString());
        userToken.setUserId(user.getId());
        userToken.setExprTime(new Date(System.currentTimeMillis() + 30 * 60 * 1000));
        userToken.setType(UserToken.Type.WEB);
        sqlManager.insert(userToken, true);
        //set cache
        request.getSession().setAttribute(Token, userToken.getToken());
        request.getSession().setAttribute(Uid, userToken.getUserId());

        response.addCookie(new Cookie(Token, userToken.getToken()));
        return Result.ok(
            C.newMap(
                "token", userToken.getToken()
                , "user", user
            )
        );
    }



}
