package com.beeasy.hzback.modules.setting.dao;

import com.beeasy.hzback.core.helper.SpringContextUtils;
import com.beeasy.hzback.modules.setting.entity.Role;
import com.beeasy.hzback.modules.setting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
}