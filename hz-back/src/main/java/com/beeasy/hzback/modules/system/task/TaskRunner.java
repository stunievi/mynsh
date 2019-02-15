package com.beeasy.hzback.modules.system.task;

import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.entity.UserToken;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.SQLManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class TaskRunner {

    @Autowired
    @Qualifier(value = "sqlManagers")
    Map<String, SQLManager> sqlManagers;

    /**
     * 定时清理登录令牌(每天失效)
     */
    @Scheduled(cron = "0 10 0 ? * *")
    public void clearTokens(){
        sqlManagers.forEach((s, sqlManager) -> {
            sqlManager.lambdaQuery(UserToken.class).delete();
        });
    }

}
