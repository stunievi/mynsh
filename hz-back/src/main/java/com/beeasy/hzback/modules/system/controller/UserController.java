package com.beeasy.hzback.modules.system.controller;

import bin.leblanc.classtranslate.Transformer;
import bin.leblanc.zed.Zed;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.util.CrUtils;
import com.beeasy.hzback.modules.setting.dao.IUserDao;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.setting.service.UserService;
import com.beeasy.hzback.modules.system.dao.IQuartersDao;
import com.beeasy.hzback.modules.system.entity.Quarters;
import com.beeasy.hzback.modules.system.form.ChangePassword;
import com.beeasy.hzback.modules.system.form.Pager;
import com.beeasy.hzback.modules.system.form.UserAdd;
import com.beeasy.hzback.modules.system.form.UserEdit;
import com.beeasy.hzback.modules.system.zed.UserZed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


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
    UserService userService;

    UserZed userZed = Zed.createProxy(UserZed.class);

    @Autowired
    EntityManager entityManager;


    @ApiOperation(value = "添加用户",notes = "")
    @PostMapping
    public Result add(
            @Valid UserAdd edit,
            BindingResult bindingResult
            ){
        if(bindingResult.hasErrors()){
            return Result.error(bindingResult);
        }
        //验证用户名和手机是否重复
        List<User> users = userDao.findByUsernameOrPhone(edit.getUsername(),edit.getPhone());
        if(users.size() > 0){
            return Result.error("已有相同的用户名或手机号");
        }

        User u = Transformer.transform(edit,User.class);
        User ret = entityManager.merge(u);

        return ret.getId() > 0 ? Result.ok() : Result.error();
    }

    @ApiOperation(value = "修改用户", notes = "")
    @PutMapping
    public Result edit(
            @Valid UserEdit edit,
            BindingResult bindingResult
    ){
        if(bindingResult.hasErrors()) return Result.error(bindingResult);
        //验证用户名和手机是否重复
        List<User> users = userDao.findByUsernameOrPhone(null,edit.getPhone());
        if(users.size() > 0){
            //如果当前用户的手机号一样,那么不改
            if(users.stream().allMatch(u -> !u.getPhone().equals(edit.getPhone()))){
                return Result.error("已有相同的手机号");
            }
        }
        User u = entityManager.find(User.class,edit.getId());
        u = Transformer.transform(edit,u);
        User ret = entityManager.merge(u);
        return ret.getId() > 0 ? Result.ok() : Result.error();
    }

    @ApiOperation(value = "用户列表", notes = "查找用户列表，当传递用户名的时候，只会查找出符合条件的用户")
    @GetMapping
    public Result<Page<User>> list(
            Pager pager,
            @PageableDefault(value = 15, sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable,
            String userName
    ){
        Page<User> users;
        if(StringUtils.isEmpty(userName)){
            users = userDao.findAll(pageable);
        }
        else{
            users = userDao.findAllByUsername(userName,pageable);
        }
        return Result.ok(users);
    }

    @ApiOperation(value = "禁用/启用", notes = "批量更新状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userIds",value = "用户主键", required = true),
            @ApiImplicitParam(name = "isBaned",value = "是否禁用", required = true)
    })
    @PutMapping("/updateBaned")
    public Result<Boolean> updateBaned(
            Integer[] userIds,
            boolean isBaned
    ){
        userDao.updateBanedByIds(userIds,isBaned);
        return Result.ok();
    }

    @ApiOperation(value = "设置用户岗位", notes = "岗位设置, 需一次性传递所有岗位的ID, 无效的岗位会被略过")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true),
            @ApiImplicitParam(name = "quartersIds", value = "岗位ID", required = true)
    })
    @PutMapping("/setQuarters")
    public Result setQuarters(
            Integer userId,
            Integer[] quartersIds
    ){
        if(userId == null || quartersIds == null){
            return Result.error();
        }
        User user = userDao.findOne(userId);
        if(user == null) return Result.error("没有找到这个用户");
        List<Quarters> list = Arrays.asList(quartersIds)
                .stream()
                .map(id -> quartersDao.findOne(id))
                .filter(item -> item != null)
                .collect(Collectors.toList());
        user.setQuarters(list);
        userDao.save(user);
        return Result.ok();
    }

    @ApiOperation(value = "修改用户密码", notes = "只要登录都可以调用")
    @PutMapping("/changePassword")
    public Result changePassword(
            @Valid ChangePassword changePassword,
            BindingResult bindingResult
            ){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.getPassword().equals(CrUtils.md5(changePassword.getOldPassword().getBytes()))){
            return Result.error("旧密码不正确");
        }
        User u = userDao.findOne(user.getId());
        u.setPassword(CrUtils.md5(changePassword.getNewPassword().getBytes()));
        userDao.save(u);
        return Result.ok();
    }




}
