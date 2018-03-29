//package com.beeasy.hzback.modules.setting.controller;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.TypeReference;
//import com.beeasy.hzback.core.helper.Result;
//import com.beeasy.hzback.modules.setting.dao.IUserDao;
//import com.beeasy.hzback.modules.setting.entity.Role;
//import com.beeasy.hzback.modules.setting.entity.User;
//import com.beeasy.hzback.modules.setting.service.DepartmentService;
//import com.beeasy.hzback.modules.setting.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import java.util.Set;
//
//@Controller
//@RequestMapping("/admin/setting/permission")
//public class PermissionController {
//    @Autowired
//    IUserDao userDao;
//
//    @Autowired
//    UserService userService;
//
//    @Autowired
//    DepartmentService departmentService;
//
//    static String failedUrl = "redirect:list";
//
//
//    @GetMapping("/list")
//    public String userList(Model model, @PageableDefault(value = 15, sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable){
//        Page<User> userList = userDao.findAll(pageable);
//        model.addAttribute("list",userList);
//        return "setting/permission_list";
//    }
//
//    @GetMapping("/edit")
//    public String edit(Model model,Integer id){
//        if(id == null){
//            return failedUrl;
//        }
//        User user = userDao.findOne(id);
//        if(user == null){
//            return failedUrl;
//        }
//        try {
//            model.addAttribute("tree",JSON.toJSONString(departmentService.listAsTree()));
//            model.addAttribute("json",JSON.toJSONString(user));
//        } catch (Exception e) {
//            return failedUrl;
//        }
//        return "setting/permission_edit";
//    }
//
//    @PostMapping("/edit")
//    @ResponseBody
//    public Result edit(
//            Integer userId,
//            String roles
//    ){
//        TypeReference<Set<Role>> type = new TypeReference<Set<Role>>(){};
//        Set<Role> rolesSet = JSON.parseObject(roles,type);
//        boolean flag = userDao.updateRoles(userId,rolesSet);
//        return Result.ok();
//    }
//
//}
