package com.beeasy.hzback.modules.mobile.controller;

import com.beeasy.hzback.core.helper.RSAUtils;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.core.security.CustomUserService;
import com.beeasy.hzback.core.security.JwtTokenUtil;
import com.beeasy.hzback.modules.mobile.request.UserLoginRequest;
import com.beeasy.hzback.modules.mobile.response.UserLoginResponse;
import com.beeasy.hzback.modules.system.dao.IDepartmentDao;
import com.beeasy.hzback.modules.system.dao.IUserDao;
import com.beeasy.hzback.modules.system.entity.Department;
import com.beeasy.hzback.modules.system.entity.Quarters;
import com.beeasy.hzback.modules.system.entity.User;
import com.beeasy.hzback.modules.system.service.DepartmentService;
import com.beeasy.hzback.modules.system.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.DigestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mobile")
public class MobileUserController {
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


    private Result.Entry[] userEntries = {
            new Result.Entry(Department.class,"departments","quarters"),
            new Result.Entry(Quarters.class,"department","users")
    };

    @GetMapping("/department/list")
    public String getDepartmentsAndUsers(){
        return Result.ok(userService.findDepartmentsByParent_Id(0)).toMobile(
                new Result.Entry(Quarters.class,"department","users"),
                new Result.Entry(User.class,"quarters","permissions"));
    }

    @PostMapping("/login")
    public String login(
            @RequestBody @Valid UserLoginRequest loginRequest,
            BindingResult bindingResult
    ){
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        if(!userDetails.getPassword().equals(DigestUtils.md5DigestAsHex(loginRequest.getPassword().getBytes()))){
            return Result.error("密码错误").toJson();
        }
        String token = jwtTokenUtil.generateToken(userDetails);
        //生成秘钥对
//        KeyPairGenerator kpg;
//        try{
//            kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
//        }catch(NoSuchAlgorithmException e){
//            throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
//        }
        Map<String,String> map = RSAUtils.createKeys(1024);
        userDao.updateUserKeys(map.get("privateKey"),map.get("publicKey"),userDetails.getUsername());

        return Result.ok(new UserLoginResponse(token,userDao.findFirstByUsername(userDetails.getUsername()).orElse(null),map.get("publicKey"))).toJson(userEntries);
    }

    @PostMapping("/user/face/edit")
    public String uploadFace(@RequestParam MultipartFile file){
        return Result.finish(userService.updateUserFace(Utils.getCurrentUserId(),file)).toMobile();
    }

    @PostMapping("/users")
    public String getUserInfo(
            @RequestBody List<Long> userIds
    ){
        return Result.ok(userService.findUser(userIds)).toMobile(
            new Result.Entry(Department.class,"departments","quarters"),
            new Result.Entry(Quarters.class,"users","department")
        );
    }

    @GetMapping("/normalUsers")
    public String getNormalUsers(){
        return Result.ok(userDao.getNormalUsers()).toMobile();
    }

//    @GetMapping("/user/myself")
//    public String getSelfUserInfo(){
//        return Result.okJson(userService.findUser(Utils.getCurrentUserId()),userEntries);
//    }


    @GetMapping("/user/department/{id}")
    public String getDepartmentUsers(
            @PathVariable Long did
    ){
        return Result.ok(userDao.getSimpleUsersFromDepartment(did)).toMobile();
    }


    @ApiOperation(value = "同步全行组织结构")
    @GetMapping("/user/simpleDepartments")
    public String getAllDepartments(){
        return Result.ok(departmentDao.findAll()).toMobile(
                new Result.Entry(Department.class,"parent","departments"),
                new Result.Entry(Quarters.class,"department","users"));
    }

    @ApiOperation(value = "同步全行组织架构")
    @GetMapping("/user/department/all")
    @Deprecated
    public String getAllDepartments2(){
        return Result.ok(departmentDao.findAllByParent(null)).toMobile(
                new Result.Entry(Department.class,"parent"),
                new Result.Entry(Quarters.class,"department"),
                new Result.Entry(User.class,"quarters","permissions","departments"));
    }


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
