package com.beeasy.hzback.dao;

import com.beeasy.hzback.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserDao extends JpaRepository<User,Integer> {
//    User findByName(String userName);
    User findByUsername(String userName);
//    User findByUserNameOrEmail(String username, String email);


}