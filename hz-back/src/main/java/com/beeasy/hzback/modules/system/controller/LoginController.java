package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.core.security.CustomUserService;
import com.beeasy.hzback.core.security.JwtTokenUtil;
import com.beeasy.hzback.modules.system.dao.IUserDao;
import com.beeasy.hzback.modules.system.dao.IUserTokenDao;
import com.beeasy.common.entity.User;
import com.beeasy.common.entity.UserToken;
import com.beeasy.hzback.modules.system.response.UserInfoResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Api(tags = "登录API", description = "登录接口")
@RestController
@RequestMapping("/api")
public class LoginController {

//    @Autowired
//    RedisTemplate<String,LoginLock> redisTemplate;

    @Autowired
    CustomUserService userDetailsService;

    @Autowired
    IUserDao userDao;

    //    @Autowired
//    AuthenticationManager authenticationManager;
    @Autowired
    IUserTokenDao userTokenDao;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    Map<String, LoginLock> loginMap = new HashMap<>();

    @AllArgsConstructor
    @NoArgsConstructor
    static class LoginLock {
        private String userName;
        private int errorCount = 0;
        private long lastErrorDate;
    }


    @Transactional
    @ApiOperation(value = "登录接口", notes = "根据用户名和密码换取JWT秘钥，连续3次登录失败会被锁定15分钟不可登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true),
            @ApiImplicitParam(name = "password", value = "密码", required = true)
    })
    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public Result login(
            @RequestParam String username,
            @RequestParam String password) {
//        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
//            return Result.error();
//        }
        //如果这个账号已经被锁定，那么在超时时间内不能再登录
//        String loginLockKey = "login:lock:" + username;
//        LoginLock loginLock = loginMap.get(username);
//        if(loginLock != null){
//            if(loginLock.errorCount >= 3){
//                return Result.error("由于连续验证失败，请15分钟之后再登录");
//            }
//            if(System.currentTimeMillis() - loginLock.lastErrorDate > 15 * 60 * 1000){
//                loginMap.remove(loginLock);
//                loginLock = null;
//            }
//        }

        password = DigestUtils.md5DigestAsHex(password.getBytes());
        User user = userDao.findFirstByUsernameAndPassword(username, password).orElse(null);
//        if(null == user){
//            if(loginLock == null){
//                loginLock = new LoginLock(username,0,0);
//            }
//            loginLock.lastErrorDate = System.currentTimeMillis();
//            loginLock.errorCount++;
//            loginMap.put(username,loginLock);
////            redisTemplate.opsForValue().set(loginLockKey,loginLock, 15 * 60, TimeUnit.SECONDS);
//            return Result.error("密码错误，您还可以尝试" + (3 - loginLock.errorCount) + "次");
//        }

        if (null == user) {
            return Result.error("登录失败,用户名或密码错误");
        }
        //删除过期token
        userTokenDao.deleteAllByExprTimeLessThan(new Date());
        try {
            //查询还存在的token
            UserToken userToken = userTokenDao.findTopByUserIdAndType(user.getId(), UserToken.Type.WEB).orElse(null);
            if (null == userToken) {
                userToken = new UserToken();
                userToken.setToken(jwtTokenUtil.generateToken(user.getId()));
            }
            userToken.setUserId(user.getId());
            userToken.setExprTime(new Date(System.currentTimeMillis() + 30 * 60 * 1000));
            userToken.setType(UserToken.Type.WEB);
            userTokenDao.save(userToken);
            return Result.ok(new UserInfoResponse(userToken.getToken(), user));
        } catch (Exception e) {
            return Result.error("登录失败");
        }

    }

    @ApiOperation(value = "检查是否登录")
    @RequestMapping(value = "/checkOnline", method = RequestMethod.GET)
    public Result checkOnline() {
        return Result.finish(Utils.getCurrentUserId() > 0);
    }


//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    @RequestMapping("/test2")
//    public Result testNoPermission(){
//        return Result.ok();
//    }
//
//    @PreAuthorize("hasRole('ROLE_ADMIN2')")
//    @RequestMapping("/test3")
//    public Result testNoPermission2(){
//        return Result.ok();
//    }


}
