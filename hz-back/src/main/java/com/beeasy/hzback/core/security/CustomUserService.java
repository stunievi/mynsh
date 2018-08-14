package com.beeasy.hzback.core.security;

import com.beeasy.hzback.modules.system.dao.IUserDao;
import com.beeasy.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;

public class CustomUserService implements UserDetailsService { //自定义UserDetailsService 接口

    @Autowired
    IUserDao userDao;

//    @Autowired
//    PermissionDao permissionDao;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userDao.findByUsername(username);
        if (user != null) {
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            SecurityUser su = new SecurityUser(user, authorities);
            return su;
        } else {
            throw new UsernameNotFoundException("admin: " + username + " do not exist!");
        }
    }


}