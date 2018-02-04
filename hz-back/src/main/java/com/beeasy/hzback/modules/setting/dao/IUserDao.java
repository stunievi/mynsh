package com.beeasy.hzback.modules.setting.dao;

import com.beeasy.hzback.core.helper.SpringContextUtils;
import com.beeasy.hzback.modules.setting.entity.Department;
import com.beeasy.hzback.modules.setting.entity.Role;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.setting.entity.WorkFlow;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.*;
import java.beans.Transient;
import java.util.*;
import java.util.stream.Collectors;

public interface IUserDao extends JpaRepository<User,Integer> {
//    User findByName(String userName);
    User findByUsername(String userName);
//    User findByUserNameOrEmail(String username, String email);

    @Transactional
    default boolean updateRoles(Integer userId,Set<Role> roles) throws RuntimeException{
        if(userId == null){
            return false;
        }
        User user = this.findOne(userId);
        if(user == null){
            return false;
        }
        IRoleDao roleDao = (IRoleDao) SpringContextUtils.getBean(IRoleDao.class);

        //注意，这里需要双向解除关联，才会生效
        //解除role和user的关联
        for(Role r : user.getRoles()){
            if(r.getUsers().contains(user)){
                r.getUsers().remove(user);
                roleDao.save(r);
            }
        }
        //解除和角色的关联
        user.setRoles(new HashSet<>());
        this.save(user);

        Set<Role> updateRoles = new HashSet<>();
        //验证每一个roles是否存在
        for(Role role : roles){
            if(role.getId() == null){
                continue;
            }
            role = roleDao.findOne(role.getId());
            if(role == null){
                continue;
            }
            updateRoles.add(role);
            role.getUsers().add(user);
            roleDao.save(role);
        }
        if(updateRoles.size() == 0){
            throw new RuntimeException();
        }

        user.setRoles(updateRoles);
        User result = this.save(user);
        return result != null && result.getRoles().size() > 0;
    }

    /**
     * 得到一个用户所有的工作流
     * @return
     */
    default List<WorkFlow> getUserWorkFlows(User user){
        IDepartmentDao departmentDao = (IDepartmentDao) SpringContextUtils.getBean(IDepartmentDao.class);
        IRoleDao roleDao = (IRoleDao) SpringContextUtils.getBean(IRoleDao.class);
        List<Role> roles = roleDao.findAllByUsers(Arrays.asList(new User[]{user}));
        return roles.stream()
                .map(role -> role.getDepartment().getWorkFlows())
                .flatMap(Set::stream)
                .distinct().collect(Collectors.toList());
//        Set<Role> roles = user.getRoles();
//        List<WorkFlow> workFlows = new ArrayList<>();
//        for(Role role : roles){
//            Department department = role.getDepartment();
////            Department department = departmentDao.findOne();
//            workFlows.addAll(department.getWorkFlows());
//        }
//        return workFlows;
    }



}