package bin.leblanc.zed.config;

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
        applicationContext.publishEvent(new ZedInitializedEvent(this));


    }


}
