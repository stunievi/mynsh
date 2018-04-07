package com.beeasy.hzback.modules.system.task;

import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.core.helper.SpringContextUtils;
import com.beeasy.hzback.modules.system.dao.ISystemTaskDao;
import com.beeasy.hzback.modules.system.entity.SystemTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Component
@Slf4j
public class TaskRunner{

    @Value("${workflow.lockTimeout}")
    int lockTimeout;

    @Autowired
    ISystemTaskDao systemTaskDao;

    protected void lockTasks(Set<Long> ids) {
        systemTaskDao.lockByIds(ids,new Date());
    }

    protected void deleteTasks(Set<Long> ids) {
        systemTaskDao.deleteAllByIdIn(ids);
    }

    protected boolean doSingleTask(SystemTask task) {
        try {
            Class clz = Class.forName(task.getClassName());
            if (!ITask.class.isAssignableFrom(clz)) {
                throw new ClassNotFoundException();
            }
            ITask taskInstance = (ITask) SpringContextUtils.getBeanObject(clz);
            taskInstance.doTask(task.getTaskKey(), task.getParams());
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (RestException e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }

    @Scheduled(fixedRate = 1000)
    public void doTask() {
        //清理锁定时间超过10秒的任务
        systemTaskDao.deleteFailedLock(new Date(System.currentTimeMillis() - lockTimeout));

        Pageable pageable = new PageRequest(0, 200);
        Page<SystemTask> tasks = systemTaskDao.findAllByRunTimeBeforeAndThreadLock(new Date(),false,pageable);
        if(tasks.getContent().size() == 0) return;

        //锁定
        lockTasks(tasks.getContent().stream().map(SystemTask::getId).collect(Collectors.toSet()));

        Set<Long> ids = tasks.getContent().stream()
                .filter(task -> doSingleTask(task))
                .map(SystemTask::getId)
                .collect(Collectors.toSet());


        deleteTasks(ids);
    }

}
