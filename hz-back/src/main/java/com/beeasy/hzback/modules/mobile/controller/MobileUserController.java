package com.beeasy.hzback.modules.mobile.controller;

import bin.leblanc.dataset.DataSet;
import bin.leblanc.dataset.DataSetFactory;
import bin.leblanc.dataset.DataSetResult;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.setting.entity.Department;
import com.beeasy.hzback.modules.setting.entity.Role;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.setting.entity.UserProfile;
import com.beeasy.hzback.modules.system.entity.SystemMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MobileUserController {

    @Autowired
    DataSetFactory dataSetFactory;

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
