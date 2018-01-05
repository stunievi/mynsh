package com.beeasy.hzback.modules.setting.dao;

import com.beeasy.hzback.modules.setting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserDao extends JpaRepository<User,Integer> {
//    User findByName(String userName);
    User findByUsername(String userName);
//    User findByUserNameOrEmail(String username, String email);


}