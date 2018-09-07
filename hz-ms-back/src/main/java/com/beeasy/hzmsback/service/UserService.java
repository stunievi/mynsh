package com.beeasy.hzmsback.service;

import com.beeasy.hzmsback.request.UserAddRequeest;
import com.beeasy.mscommon.entity.User;
import org.beetl.sql.core.SQLManager;
import org.osgl.$;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

@Service
@Transactional
public class UserService {

    @Autowired
    SQLManager sqlManager;

    public User createUser(UserAddRequeest requeest){
         User u = $.copy(requeest).to(User.class);
         //password md5
         u.setPassword(DigestUtils.md5DigestAsHex(u.getPassword().getBytes()));

         return u;
    }
}
