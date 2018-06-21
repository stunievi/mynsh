package com.beeasy.hzback.modules.system.controller;

import bin.leblanc.zed.Zed;
import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.modules.system.cache.SystemConfigCache;
import com.beeasy.hzback.modules.system.dao.IQuartersDao;
import com.beeasy.hzback.modules.system.dao.IUserDao;
import com.beeasy.hzback.modules.system.entity.Department;
import com.beeasy.hzback.modules.system.entity.GlobalPermission;
import com.beeasy.hzback.modules.system.entity.Quarters;
import com.beeasy.hzback.modules.system.entity.User;
import com.beeasy.hzback.modules.system.form.UserAdd;
import com.beeasy.hzback.modules.system.form.UserEdit;
import com.beeasy.hzback.modules.system.form.UserSearch;
import com.beeasy.hzback.modules.system.service.UserService;
import com.beeasy.hzback.modules.system.zed.UserZed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;


@Api(tags = "用户API", description = "后台管理用户相关接口，需要有管理员权限")
@RestController
@Transactional
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    IUserDao userDao;

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
    @PutMapping
    public Object edit(
            @Valid UserEdit edit,
            BindingResult bindingResult
    ) throws RestException {
        return userService.editUser(edit);
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


    @GetMapping("/ids")
    public String getList(Long[] ids){
        List<User> users = userService.findUserByIds(ids);
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
        return true;
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



}
