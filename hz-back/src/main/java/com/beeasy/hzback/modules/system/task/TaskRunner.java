package com.beeasy.hzback.modules.system.task;

import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.entity.UserToken;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.SQLManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TaskRunner {

    int lockTimeout;


    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    Utils utils;
    @Autowired
    SQLManager sqlManager;

    final static String LOGIC_NODE_LOCK = "LOGIC_NODE_LOCK";


    /**
     * 定时清理登录令牌(每天失效)
     */
    @Scheduled(cron = "0 10 0 ? * *")
    public void clearTokens(){
      sqlManager.lambdaQuery(UserToken.class).delete();
    }

}
