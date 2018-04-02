//package com.beeasy.hzback.modules.setting.service;
//
//import com.beeasy.hzback.core.util.CrUtils;
//import com.beeasy.hzback.modules.setting.dao.IUserDao;
//import com.beeasy.hzback.modules.setting.entity.User;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@Transactional
//public class UserService2 {
//
//    @Autowired
//    IUserDao userDao;
//
//    public boolean add(User user){
//        if(user.getId() != null){
//            user.setId(null);
//        }
//        user.setPassword(CrUtils.md5(user.getPassword().getBytes()));
//        userDao.save(user);
//        return user.getId() > 0;
//    }
//
//    public boolean edit(User user){
//        if(user.getId() == null){
//            return false;
//        }
//        User baseUser = userDao.findOne(user.getId());
//        if(baseUser == null){
//            return false;
//        }
//        //用户名禁止修改
//        //如果修改了密码，那么重新md5
//        if(StringUtils.isEmpty(user.getPassword())){
//            baseUser.setPassword(CrUtils.md5(user.getPassword().getBytes()));
//        }
//        baseUser.setBaned(user.isBaned());
//        userDao.save(baseUser);
//        return true;
//    }
//
//    public boolean delete(long id){
//        userDao.delete(id);
//    }
//}
