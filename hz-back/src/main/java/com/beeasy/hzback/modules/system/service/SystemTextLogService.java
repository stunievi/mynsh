package com.beeasy.hzback.modules.system.service;

import com.beeasy.hzback.modules.system.dao.ISystemTextLogDao;
import com.beeasy.common.entity.SystemTextLog;
import com.beeasy.hzback.modules.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class SystemTextLogService {
    @Autowired
    ISystemTextLogDao systemTextLogDao;
    @Autowired
    UserService userService;

    @Async
    public void addLog(SystemTextLog.Type type, long linkId, long uid, String content) {
        SystemTextLog log = new SystemTextLog();
        log.setType(type);
        log.setLinkId(linkId);
        log.setContent(content);
        log.setUserId(uid);
        systemTextLogDao.save(log);
    }

    @Async
    public void handleSystemLog() {

    }
}
