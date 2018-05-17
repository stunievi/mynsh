package com.beeasy.hzback.modules.mobile.controller;

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
        return Result.okJson(departmentService.findDepartments(null,null),
                new Result.Entry(Quarters.class,"department"),
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
        return Result.ok(new UserLoginResponse(token,userDao.findFirstByUsername(userDetails.getUsername()).orElse(null))).toJson(userEntries);
    }

    @PostMapping("/user/face/edit")
    public Result<Long> uploadFace(@RequestParam MultipartFile file){
        return Result.finish(userService.updateUserFace(Utils.getCurrentUserId(),file));
    }

    @PostMapping("/users")
    public String getUserInfo(
            @RequestBody List<Long> userIds
    ){
        return Result.okJson(userService.findUser(userIds),
            new Result.Entry(Department.class,"departments","quarters"),
            new Result.Entry(Quarters.class,"users","department")
        );
    }

    @GetMapping("/normalUsers")
    public Result getNormalUsers(){
        return Result.ok(userDao.getNormalUsers());
    }

//    @GetMapping("/user/myself")
//    public String getSelfUserInfo(){
//        return Result.okJson(userService.findUser(Utils.getCurrentUserId()),userEntries);
//    }


    @GetMapping("/user/department/{id}")
    public Result getDepartmentUsers(
            @PathVariable Long did
    ){
        return Result.ok(userDao.getSimpleUsersFromDepartment(did));
    }


    @ApiOperation(value = "同步全行组织结构")
    @GetMapping("/user/simpleDepartments")
    public String getAllDepartments(){
        return Result.okJson(departmentDao.findAll(),
                new Result.Entry(Department.class,"parent","departments"),
                new Result.Entry(Quarters.class,"department","users"));
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
