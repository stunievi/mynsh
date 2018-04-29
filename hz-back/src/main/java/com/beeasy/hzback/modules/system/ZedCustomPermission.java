package com.beeasy.hzback.modules.system;

import bin.leblanc.zed.Zed;
import bin.leblanc.zed.event.ZedInitializedEvent;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.entity.CloudDirectoryIndex;
import com.beeasy.hzback.modules.system.entity.CloudFileIndex;
import com.beeasy.hzback.modules.system.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Root;

@Slf4j
@Component
public class ZedCustomPermission implements ApplicationListener<ZedInitializedEvent> {
    @Autowired
    Zed zed;

    @Override
    public void onApplicationEvent(ZedInitializedEvent zedInitializedEvent) {

        zed.addRoleHandler(token -> {
            if(token.equals("SU")){
                return "SU";
            }
            /**
             * 因为已经做过验证了，token就不需要了
              */
//            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//            Collection<GrantedAuthority> auths = (Collection<GrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
            User user = Utils.getCurrentUser();
            if(user.getUsername().equals("1")){
                return "fuck";
            }
            /**
             * 检查出最后的角色名
             */
            return "admin";

        });

        zed.addRole("fuck",role -> {
            role.disallowAllDelete();
            role.disallowAllPost();
            role.disallowAllPut();
            role.createEntityPermission(CloudDirectoryIndex.class)
                    .allowGet()
                    .setGetWhereLimit((cb,root,condition) -> {
                        return cb.equal(root.get("user"),(Utils.getCurrentUser()));
                    });
            role.createEntityPermission(CloudFileIndex.class)
                    .allowGet()
                    .setGetWhereLimit((cb,root,condition) -> {
                        root = (Root) root;
                        return cb.equal(((Root) root).get("directoryIndex").get("user"),Utils.getCurrentUser());
                    });
        });

        zed.addRole("admin",role -> {
//            role.createEntityPermission(SystemMenu.class)
//                    //只允许查询权限
//                    .allowGet()
//                    //限定查找范围，只能查找自己的菜单
//                    .setGetWhereLimit((cb,root,condition) -> {
//                        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//                        log.info(user.getUsername());
//                        Root r = (Root)root;
//                        return cb.and(r.join("users").in(user.getId()));
//                    });
        });
    }
}
