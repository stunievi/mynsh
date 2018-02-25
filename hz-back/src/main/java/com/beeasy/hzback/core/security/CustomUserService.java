package com.beeasy.hzback.core.security;

import com.beeasy.hzback.modules.setting.dao.IUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by yangyibo on 17/1/18.
 */
@Service
public class CustomUserService implements UserDetailsService { //自定义UserDetailsService 接口

    @Autowired
    IUserDao userDao;

//    @Autowired
//    PermissionDao permissionDao;

    @Override
    public UserDetails loadUserByUsername(String username) {
        com.beeasy.hzback.modules.setting.entity.User user = userDao.findByUsername(username);
        if (user != null) {
//            List<Permission> permissions = permissionDao.findByAdminUserId(user.getId());
//            List<GrantedAuthority> grantedAuthorities = new ArrayList <>();
//            for (Permission permission : permissions) {
//                if (permission != null && permission.getName()!=null) {
//
//                    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(permission.getName());
//                    //1：此处将权限信息添加到 GrantedAuthority 对象中，在后面进行全权限验证时会使用GrantedAuthority 对象。
//                    grantedAuthorities.add(grantedAuthority);
//                }
//            }
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            SecurityUser su = new SecurityUser(user,authorities);
            return su;
        } else {
            throw new UsernameNotFoundException("admin: " + username + " do not exist!");
        }
    }

}