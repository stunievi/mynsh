package com.beeasy.hzback.modules.system.controller;

import bin.leblanc.zed.Zed;
import com.alibaba.fastjson.JSONArray;
import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.cache.SystemConfigCache;
import com.beeasy.hzback.modules.system.dao.IQuartersDao;
import com.beeasy.hzback.modules.system.dao.IRoleDao;
import com.beeasy.hzback.modules.system.dao.IUserDao;
import com.beeasy.hzback.modules.system.entity.*;
import com.beeasy.hzback.modules.system.form.*;
import com.beeasy.hzback.modules.system.log.NotSaveLog;
import com.beeasy.hzback.modules.system.service.UserService;
import com.beeasy.hzback.modules.system.zed.UserZed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Api(tags = "用户API", description = "后台管理用户相关接口，需要有管理员权限")
@RestController
@Transactional
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    IUserDao userDao;
    @Autowired
    IRoleDao roleDao;

    @Autowired
    IQuartersDao quartersDao;

    @Autowired
    SystemConfigCache cache;


    UserZed userZed = Zed.createProxy(UserZed.class);

    @Autowired
    EntityManager entityManager;

    @Autowired
    UserService userService;

    @ApiOperation(value = "添加用户",notes = "")
    @PostMapping
    public Result<User> add(
            @Valid UserAdd edit,
            BindingResult bindingResult
            ) throws RestException {

        return userService.createUser(edit);
    }

    @ApiOperation(value = "修改用户", notes = "")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Result edit(
            @Valid @RequestBody UserEdit edit
    ) throws RestException {
        return userService.editUser(edit);
    }

    @ApiOperation(value = "批量删除用户")
    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public Result deleteUser(
            @RequestParam String id
    ){
        return Result.ok(userService.deleteUser(Utils.convertIdsToList(id)));
    }

    @ApiOperation(value = "批量添加岗位")
    @RequestMapping(value = "/addUsersToQuarters", method = RequestMethod.GET)
    public Result userAddQuarter(
            @RequestParam String uids,
            @RequestParam long qid
    ){
        return userService.addUsersToQuarters(Utils.convertIdsToList(uids), qid);
    }

    @ApiOperation(value = "用户列表", notes = "查找用户列表，当传递用户名的时候，只会查找出符合条件的用户")
    @GetMapping
    public String list(
            @PageableDefault(value = 15, sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable,
            UserSearch search
//            String userName
    ){
        return Result.ok(userService.searchUser(search,pageable)).toJson(
                new Result.Entry(Department.class,"children"),
                new Result.Entry(User.class,"departments","password")
                );
//        Department::getChildren;
//        PropertyFilter propertyFilter = (source,name,value) -> {
//            if(source instanceof Department){
//                if(name.equals("children")) return false;
//            }
//            else if(source)
//            return true;
//        };
//        return JSON.toJSONString(Result.ok(userService.searchUser(search,pageable)),propertyFilter);
//        String json = JSON.toJSONString(userService.searchUser(search,pageable),propertyFilter);
//        return Result.ok(userService.searchUser(search,pageable));
    }


    @RequestMapping(value = "/ids",method = RequestMethod.GET)
    public String getList(
            @RequestParam String ids){
        List<User> users = userService.findUser(Utils.convertIdsToList(ids));
        return Result.okJson(users,
                new Result.Entry(Department.class,"children"),
                new Result.Entry(Quarters.class,"department","users")
                );
    }

    @ApiOperation(value = "禁用/启用", notes = "批量更新状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userIds",value = "用户主键", required = true,allowMultiple = true),
            @ApiImplicitParam(name = "isBaned",value = "是否禁用", required = true)
    })
    @PutMapping("/updateBaned")
    public Object updateBaned(
            long[] userIds,
            boolean isBaned
    ){
        userDao.updateBanedByIds(userIds,isBaned);
        return Result.ok();
    }


    @ApiOperation(value = "用户功能模块授权")
    @RequestMapping(value = "/permission/set", method = RequestMethod.POST)
    public Result setUserMethodPermission(
            @RequestParam long uid,
            @Valid @RequestBody JSONArray array
    ){
        return Result.ok(userService.addGlobalPermission(GlobalPermission.Type.USER_METHOD, 0, GlobalPermission.UserType.USER, Collections.singleton(uid),array));
    }

    @ApiOperation(value = "查看某个人的功能模块授权")
    @RequestMapping(value = "/permission/get", method = RequestMethod.GET)
    public Result getUserMethodPermissions(@RequestParam long uid){
        return Result.ok(userService.getGlobalPermission(GlobalPermission.Type.USER_METHOD,0, GlobalPermission.UserType.USER,uid));
    }


    @ApiOperation(value = "修改密码")
    @RequestMapping(value = "/modifyPassword", method = RequestMethod.POST)
    public Result modifyPassword(
            @Valid @RequestBody ModifyPasswordRequest request
    ){
        return userService.modifyPassword(Utils.getCurrentUserId(), request.getOldPassword(), request.getNewPassword());
    }

    @ApiOperation(value = "修改个人资料")
    @RequestMapping(value = "/modifyProfile", method = RequestMethod.POST)
    public Result modifyProfile(
            @Valid @RequestBody ProfileEditRequest request
    ){
        return userService.modifyProfile(Utils.getCurrentUserId(), request);
    }


    @ApiOperation(value = "得到我的功能授权")
    @RequestMapping(value = "/myMethods", method = RequestMethod.GET)
    public Result getMyMethods(){
        return Result.ok(userService.getUserMethods(Utils.getCurrentUserId()));
    }




//    @ApiOperation(value = "设置用户岗位", notes = "岗位设置, 需一次性传递所有岗位的ID, 无效的岗位会被略过")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "userId", value = "用户ID", required = true),
//            @ApiImplicitParam(name = "quartersIds", value = "岗位ID", required = true)
//    })
//    @PutMapping("/setQuarters")
//    public Result setQuarters(
//            long userId,
//            long[] quartersIds
//    ) throws CannotFindEntityException {
//        userService.setQuarters(userId,quartersIds);
//        return Result.ok();
////
////        User user = userDao.findOne(userId);
////        if(user == null) return Result.error("没有找到这个用户");
////        List<Quarters> list = Arrays.asList(quartersIds)
////                .stream()
////                .map(id -> quartersDao.findOne(id))
////                .filter(item -> item != null)
////                .collect(Collectors.toList());
//////        user.setQuarters(list);
////        userDao.save(user);
////        return Result.ok();
//    }

//    @ApiOperation(value = "修改用户密码", notes = "只要登录都可以调用")
//    @PutMapping("/changePassword")
//    public Result changePassword(
//            @Valid ChangePassword changePassword,
//            BindingResult bindingResult
//            ){
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if(!user.getPassword().equals(CrUtils.md5(changePassword.getOldPassword().getBytes()))){
//            return Result.error("旧密码不正确");
//        }
//        User u = userDao.findOne(user.getId());
//        u.setPassword(CrUtils.md5(changePassword.getNewPassword().getBytes()));
//        userDao.save(u);
//        return Result.ok();
//    }


//    @ApiOperation(value = "设置用户的功能角色", notes = "采用黑名单模式, 只需要传入禁用的菜单名, 层级使用.分割, 例如菜单1.子菜单2.子菜单3")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "menus", value = "菜单名", allowMultiple = true)
//    })
//    @PutMapping("/setUnbindMenus")
//    public Object setUnbindMenus(String[] menus){
//        return userService.setUnbindMenus(Utils.getCurrentUserId(),menus);
//    }
//


    @GetMapping("/normalUsers")
    public Result getNormalUsers(){
        return Result.ok(userDao.getNormalUsers());
    }

    @NotSaveLog
    @PostMapping("/face/edit")
    public String uploadFace(@RequestParam MultipartFile file){
        return (userService.updateUserFace(Utils.getCurrentUserId(),file)).toJson();
    }


    /************ 角色相关 ************/
    @ApiOperation(value = "创建角色")
    @RequestMapping(value = "/role/create", method = RequestMethod.POST)
    public Result createRole(
            @Validated(value = RoleRequest.add.class) @RequestBody RoleRequest request
    ){
        return userService.createRole(request);
    }

    @ApiOperation(value = "编辑角色")
    @RequestMapping(value = "/role/edit", method = RequestMethod.POST)
    public Result editRole(
            @Validated(value = RoleRequest.edit.class) @RequestBody RoleRequest request
    ){
        return userService.editRole(request);
    }

    @ApiOperation(value = "编辑角色")
    @RequestMapping(value = "/role/delete", method = RequestMethod.GET)
    public Result deleteRoles(
            @RequestParam String id
    ){
        return userService.deleteRoles(Utils.convertIdsToList(id));
    }
    @RequestMapping(value = "/role/addUsers", method = RequestMethod.GET)
    @ApiOperation(value = "角色批量添加用户")
    public Result roleAddUsers(
            @RequestParam long rid,
            @RequestParam String uid
    ){
        return userService.roleAddUsers(rid, Utils.convertIdsToList(uid));
    }

    @RequestMapping(value = "/role/deleteUsers", method = RequestMethod.GET)
    @ApiOperation(value = "角色批量删除用户")
    public Result roleDeleteUsers(
            @RequestParam long rid,
            @RequestParam String uid
    ){
        return userService.roleDeleteUsers(rid, Utils.convertIdsToList(uid));
    }

    @ApiOperation(value = "用户批量设置角色")
    @RequestMapping(value = "/setRoles", method = RequestMethod.GET)
    public Result userSetRoles(
            @RequestParam long uid,
            @RequestParam String rid
    ){
        return userService.userSetRoles(uid, Utils.convertIdsToList(rid));
    }

    @ApiOperation(value = "用户批量删除角色")
    @RequestMapping(value = "/deleteRoles", method = RequestMethod.GET)
    public Result userDeleteRoles(
            @RequestParam String id
    ){
        return userService.userDeleteRoles(Utils.getCurrentUserId(), Utils.convertIdsToList(id));
    }

    @ApiOperation(value = "查询角色")
    @RequestMapping(value = "/role/getList", method = RequestMethod.GET)
    public Result getAllRoles(
            UserService.RoleSearchRequest request,
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(userService.searchRoles(request,pageable));
    }

    @ApiOperation(value = "通过ID查找指定角色")
    @RequestMapping(value = "/role/getListById", method = RequestMethod.GET)
    public Result getRoleByIds(
            @RequestParam String id
    ){
        return Result.ok(roleDao.findAllByIdIn(Utils.convertIdsToList(id)));
    }

    @ApiOperation(value = "通过用户ID查找用户所持有的角色")
    @RequestMapping(value = "/role/getRolesByUser", method = RequestMethod.GET)
    public Result getUserRoles(
            @RequestParam String id
    ){
        Map map = userDao.findAllByIdIn(Utils.convertIdsToList(id)).stream()
                .map(u -> new Object[]{u.getId() + "",u.getRoles()})
                .collect(Collectors.toMap(o -> o[0],o -> o[1]));
        return Result.ok(map);
    }

    @ApiOperation(value = "通过角色ID查找所有用户")
    @RequestMapping(value = "/role/getUsersByRole", method = RequestMethod.GET)
    public Result getRoleUsers(
            @RequestParam String id
    ){
        Map map = roleDao.findAllByIdIn(Utils.convertIdsToList(id)).stream()
                .map(r -> new Object[]{r.getId() + "",r.getUsers()})
                .collect(Collectors.toMap(o -> o[0],o -> o[1]));
        return Result.ok(map);
    }


    @ApiOperation(value = "增加角色功能模块授权")
    @RequestMapping(value = "/role/permission/method/set", method = RequestMethod.POST)
    public Result setRoleMethodPermissions(
            @RequestParam long rid,
            @Validated @RequestBody JSONArray array
    ){
        return Result.ok(userService.addGlobalPermission(GlobalPermission.Type.USER_METHOD,0, GlobalPermission.UserType.ROLE, Collections.singleton(rid), array));
    }

    @ApiOperation(value = "得到指定角色的功能模块授权")
    @RequestMapping(value = "/role/permission/method/get", method = RequestMethod.GET)
    public Result getRoleMethodPermissions(
            @RequestParam long rid
    ){
        return Result.ok(userService.getGlobalPermission(GlobalPermission.Type.USER_METHOD,0, GlobalPermission.UserType.ROLE,rid).orElse(null));
    }


    @RequestMapping(value = "/getCloudFileAccount")
    public Result getCloudFileAccount(){
        User user = userService.findUser(Utils.getCurrentUserId()).orElse(null);
        if(null == user){
            return Result.error();
        }
        return Result.ok(new Object[]{user.getProfile().getCloudUsername(),user.getProfile().getCloudPassword()});
    }

//    public Result userSetRoles(){
//
//    }
//    public Result userDeleteRoles(){
//
//    }
}
