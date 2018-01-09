package com.beeasy.hzback.modules.setting.service;

import com.beeasy.hzback.modules.setting.dao.IUserDao;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.util.CrUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    IUserDao userDao;

    public boolean add(User user){
        if(user.getId() != null){
            user.setId(null);
        }
        user.setPassword(CrUtils.md5(user.getPassword().getBytes()));
        userDao.save(user);
        return user.getId() > 0;
    }
}
