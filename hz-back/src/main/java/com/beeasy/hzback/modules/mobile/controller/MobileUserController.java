package com.beeasy.hzback.modules.mobile.controller;

import com.beeasy.hzback.core.helper.RSAUtils;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.core.security.CustomUserService;
import com.beeasy.hzback.core.security.JwtTokenUtil;
import com.beeasy.hzback.modules.cloud.CloudApi;
import com.beeasy.hzback.modules.mobile.request.UserLoginRequest;
import com.beeasy.hzback.modules.mobile.response.UserLoginResponse;
import com.beeasy.hzback.modules.system.dao.IDepartmentDao;
import com.beeasy.hzback.modules.system.dao.IUserDao;
import com.beeasy.common.entity.Department;
import com.beeasy.common.entity.Quarters;
import com.beeasy.common.entity.User;
import com.beeasy.hzback.modules.system.service.DepartmentService;
import com.beeasy.hzback.modules.system.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mobile")
public class MobileUserController {
    @Value("${filecloud.commonRootId}")
    Long CLOUD_COMMON_ROOT_ID;

    @Autowired
    CustomUserService userDetailsService;

    @Autowired
    DepartmentService departmentService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    UserService userService;

    @Autowired
    IUserDao userDao;
    @Autowired
    IDepartmentDao departmentDao;
    //    @Autowired
    CloudApi cloudApi;


    private Result.DisallowEntry[] userEntries = {
            new Result.DisallowEntry(Department.class, "departments", "quarters"),
            new Result.DisallowEntry(Quarters.class, "department", "users")
    };

    @GetMapping("/department/list")
    public String getDepartmentsAndUsers() {
        return Result.ok(userService.findDepartmentsByParent_Id(0)).toMobile(
                new Result.DisallowEntry(Quarters.class, "department", "users"),
                new Result.DisallowEntry(User.class, "quarters", "permissions"));
    }

    @GetMapping("/login/{username}/{password}")
    public String login(
            @PathVariable String username,
            @PathVariable String password
    ) {
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        User user = userDao.findFirstByUsernameAndPassword(username, password).orElse(null);
        if (null == user) {
            return Result.error("密码错误").toJson();
        }
        String token = jwtTokenUtil.generateToken(user.getId());
        Map<String, String> map = RSAUtils.createKeys(1024);
        userDao.updateUserKeys(map.get("privateKey"), map.get("publicKey"), user.getUsername());
        return Result.ok(new UserLoginResponse(token, user, map.get("publicKey"), CLOUD_COMMON_ROOT_ID)).toJson(userEntries);
    }

    @PostMapping("/login")
    public String login(
            @RequestBody @Valid UserLoginRequest loginRequest
    ) {
        loginRequest.setPassword(DigestUtils.md5DigestAsHex(loginRequest.getPassword().getBytes()));
        User user = userDao.findFirstByUsernameAndPassword(loginRequest.getUsername(), loginRequest.getPassword()).orElse(null);
        if (null == user) {
            return Result.error("密码错误").toJson();
        }
        String token = jwtTokenUtil.generateToken(user.getId());
        Map<String, String> map = RSAUtils.createKeys(1024);
        userDao.updateUserKeys(map.get("privateKey"), map.get("publicKey"), user.getUsername());
        UserLoginResponse response = new UserLoginResponse(token, user, map.get("publicKey"), CLOUD_COMMON_ROOT_ID);
//        String[] privateUser = userService.getPrivateCloudUsername(user.getId());
//        String[] commonUser = userService.getCommonCloudUsername(user.getId());
//        response.setCloudUsername(privateUser[0]);
//        response.setCloudPassword(privateUser[1]);
//        response.setCloudCommonUsername(commonUser[0]);
//        response.setCloudCommonPassword(commonUser[1]);
        return Result.ok(response).toJson(userEntries);
    }

    @PostMapping("/user/face/edit")
    public String uploadFace(@RequestParam MultipartFile file) {
        return null;
//        return (userService.updateUserFace(Utils.getCurrentUserId(),file)).toMobile();
    }

    @PostMapping("/users")
    public String getUserInfo(
            @RequestBody List<Long> userIds
    ) {
        return Result.ok(userService.findUser(userIds)).toMobile(
                new Result.DisallowEntry(Department.class, "departments", "quarters"),
                new Result.DisallowEntry(Quarters.class, "users", "department")
        );
    }

    @GetMapping("/normalUsers")
    public String getNormalUsers() {
        return Result.ok(userDao.getNormalUsers()).toMobile();
    }

//    @GetMapping("/user/myself")
//    public String getSelfUserInfo(){
//        return Result.okJson(userService.findUser(Utils.getCurrentUserId()),userEntries);
//    }


    @GetMapping("/user/department/{id}")
    public String getDepartmentUsers(
            @PathVariable Long did
    ) {
        return Result.ok(userDao.getSimpleUsersFromDepartment(did)).toMobile();
    }


    @ApiOperation(value = "同步全行组织结构")
    @GetMapping("/user/simpleDepartments")
    public String getAllDepartments() {
        return Result.ok(departmentDao.findAll()).toMobile(
                new Result.DisallowEntry(Department.class, "parent", "departments"),
                new Result.DisallowEntry(Quarters.class, "department", "users"));
    }

    @ApiOperation(value = "同步全行组织架构")
    @GetMapping("/user/department/all")
    @Deprecated
    public String getAllDepartments2() {
        return Result.ok(departmentDao.findAllByParent(null)).toMobile(
                new Result.DisallowEntry(Department.class, "parent"),
                new Result.DisallowEntry(Quarters.class, "department"),
                new Result.DisallowEntry(User.class, "quarters", "permissions", "departments"));
    }


    @ApiOperation(value = "登录私有云系统")
    @GetMapping("/user/loginCloud")
    public String loginFileCloudSystem() {
        return "";
//        return userService.loginFileCloudSystem(Utils.getCurrentUserId()).toJson();
    }

    @ApiOperation(value = "登录公共文件柜")
    @GetMapping("/user/loginCloudCommon")
    public String loginFileCloudCommonSystem() {
        return "";
//        return userService.loginFileCloudCommonSystem(Utils.getCurrentUserId()).toJson();
    }

//    @GetMapping("/department/{did}/uids")
//    public String getUidsFromDepartment(@PathVariable long did){
//        System.out.println("fuck");
//        return Result.ok(userDao.getUidsFromDepartment(Collections.singleton(did))).toJson();
//    }

//    @RequestMapping("/userInfo")
//    public Result getUserInfo(){
//        DataSet dataSet = dataSetFactory.createDataSet();
//        dataSet
//                .addMain(User.class, model -> {
//                })
//                .addExtern("profile", UserProfile.class,model -> {
//                    model.setPath("profile")
//                        .setMultipul(false);
//                })
//                .addExtern("rs",Role.class, model -> {
//                    model
//                            .setPath("roles");
//                })
//                .addExtern("ds",Department.class, model -> {
//                    model
//                            .setPath("roles","department");
//                })
//                .addExtern("menus",SystemMenu.class, model -> {
//                    model.setPath("systemMenus");
//                });
//        User user = Utils.getCurrentUser();
//        if(user == null) return Result.error();
//
//        DataSetResult result = dataSet.newSearch();
//        JSONObject ret = result
//                .clearCondition()
//                .addCondition("id",user.getId())
//                .search();
//        return Result.ok(ret);
//    }
}
