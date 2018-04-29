package com.beeasy.hzback.modules.system.controller;


import bin.leblanc.dataset.DataSetFactory;
import com.beeasy.hzback.core.helper.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


@RestController
public class SystemCommonController {


    @Autowired
    DataSetFactory dataSetFactory;

//    @PostMapping("/api/userInfo")
//    @ApiOperation(value = "用户信息数据集合", notes = "得到自己的所有相关信息")
//    public Result<UserInfoResponse> getUserInfo(){
//        DataSet dataSet = dataSetFactory.createDataSet();
//        dataSet
//                .addMain(User.class, model -> {
//                })
//                .addExtern("profile", UserProfile.class, model -> {
//                    model.setPath("profile")
//                            .setMultipul(false);
//                })
////                .addExtern("rs",Role.class, model -> {
////                    model.setPath("roles");
////                })
//                .addExtern("ds",Department.class, model -> {
//                    model.setPath("roles","department");
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


    @ApiOperation(value = "测试接口", notes = "该接口无实际作用")
    @RequestMapping("/open/test")
    public Result test(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return Result.ok("method is " + request.getMethod());
    }

    @ApiOperation(value = "测试接口2", notes = "该接口无实际作用")
    @RequestMapping("/open/test2")
    public Object test2(){
        return Math.random() > 0.5 ? true : false;
    }
}
