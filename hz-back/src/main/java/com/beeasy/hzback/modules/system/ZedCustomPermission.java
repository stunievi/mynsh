package com.beeasy.hzback.modules.system;

import bin.leblanc.zed.Zed;
import bin.leblanc.zed.event.ZedInitializedEvent;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.system.entity.SystemMenu;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Root;
import java.util.Collection;

@Slf4j
@Component
public class ZedCustomPermission implements ApplicationListener<ZedInitializedEvent> {
    @Autowired
    Zed zed;

    @Override
    public void onApplicationEvent(ZedInitializedEvent zedInitializedEvent) {

        zed.addRoleHandler(token -> {
            /**
             * 因为已经做过验证了，token就不需要了
              */
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Collection<GrantedAuthority> auths = (Collection<GrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
            /**
             * 检查出最后的角色名
             */
            return "admin";

        });

        zed.addRole("admin",role -> {
//            role.allowAllGet();


            role.createEntityPermission(SystemMenu.class)
                    .allowGet()
                    .setGetWhereLimit((cb,root,condition) -> {
                        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                        log.info(user.getUsername());
                        Root r = (Root)root;
                        return cb.and(r.join("users").in(user.getId()));
                    });
        });
    }
}
