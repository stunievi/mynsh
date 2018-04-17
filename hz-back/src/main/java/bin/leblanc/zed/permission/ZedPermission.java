package bin.leblanc.zed.permission;

import bin.leblanc.zed.Zed;
import bin.leblanc.zed.event.ZedInitializedEvent;
import com.beeasy.hzback.modules.system.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.util.Arrays;

@Slf4j
@Component
public class ZedPermission implements ApplicationListener<ZedInitializedEvent>{


    @Autowired
    private Zed zed;

    @Override
    public void onApplicationEvent(ZedInitializedEvent zedInitializedEvent) {
        log.info("开始初始化权限");
        /**
         *
         *
         * zed.addRole(roleName,checkRole,
         */


        //注册角色检查
        zed.addRoleHandler(token -> {
            String tokenStr = (String) token;
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(user.getId() == 1){
                return "SU";
            }
            if(tokenStr.indexOf("TEST") == 0){
                return tokenStr.toLowerCase();
            }
            return null;
        });


        zed.addRole("test",role -> {
//            role.createEntityPermission(User.class);
        });

        zed.addRole("test2",role -> {
            role.createEntityPermission(User.class)
                    .allowGet()
                    .setGetReturnFields(new String[]{"id","username"});
        });

        zed.addRole("test3",role -> {
            role.createEntityPermission(User.class)
                    .allowGet()
                    .setUniqueWhereFields(new String[]{"username"});

        });

        zed.addRole("test4",role -> {
            role.createEntityPermission(User.class)
                    .allowGet()
                    .setGetWhereLimit((cb, root, condition) -> {
                        //限制ID只能在1和10之间取
                        Predicate c = root.get("id").in(Arrays.asList(new Integer[]{9}));
                        return c;
                    });
        });
    }
}
