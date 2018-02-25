package bin.leblanc.zed.config;

import bin.leblanc.zed.RolePermission;
import bin.leblanc.zed.Zed;
import bin.leblanc.zed.event.ZedInitializedEvent;
import com.beeasy.hzback.modules.setting.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.persistence.criteria.Predicate;
import java.util.Arrays;

@Configuration
@Slf4j
public class ZedConfiguration implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    Zed zed;

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        zed.init();

        //基础需要注册超级管理员权限
        zed.addRole(RolePermission.SUPERUSER,(role) -> {
            role.allowAllGet();
            role.allowAllPost();
            role.allowAllPut();
            role.allowAllDelete();
        });
        zed.addRole(RolePermission.UNKNOWN,role -> {
            role.disallowAllDelete();
            role.disallowAllGet();
            role.disallowAllPost();
            role.disallowAllPut();
        });

        zed.addRoleHandler(token -> {
            //暂时用SU
            if(token.equals("SU")){
                return RolePermission.SUPERUSER;
            }
            return null;
        });

        applicationContext.publishEvent(new ZedInitializedEvent(this));


    }


}
