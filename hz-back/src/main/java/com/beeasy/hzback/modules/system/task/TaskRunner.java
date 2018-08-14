package com.beeasy.hzback.modules.system.task;

import com.beeasy.hzback.core.helper.Utils;
//import com.beeasy.hzback.modules.system.dao.ISystemTaskDao;
import com.beeasy.hzback.modules.system.dao.IWorkflowNodeInstanceDao;
import com.beeasy.common.entity.WorkflowNode;
import com.beeasy.common.entity.WorkflowNodeInstance;
import com.beeasy.hzback.modules.system.service.WorkflowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
@Slf4j
public class TaskRunner {

    @Value("${workflow.lockTimeout}")
    int lockTimeout;

    @Autowired
    IWorkflowNodeInstanceDao nodeInstanceDao;
    //    @Autowired
//    ISystemTaskDao systemTaskDao;
    @Autowired
    WorkflowService workflowService;

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    Utils utils;

    final static String LOGIC_NODE_LOCK = "LOGIC_NODE_LOCK";


    private void doLogicNodeTask() {
        Page<WorkflowNodeInstance> nodeInstances = nodeInstanceDao.getCurrentNode(WorkflowNode.Type.logic, new PageRequest(0, 200));
        if (nodeInstances.getContent().size() == 0) return;

        nodeInstances.getContent().forEach(nodeInstance -> {
            workflowService.runLogicNode(nodeInstance.getInstance(), nodeInstance);
        });

    }


    //    @Scheduled(fixedRate = 5000)
    public void doTask() {
        if (utils.isLockingOrLockFailed(LOGIC_NODE_LOCK, 10)) {
            return;
        }

        try {
            //争抢线程
            doLogicNodeTask();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            utils.unlock(LOGIC_NODE_LOCK);
        }
    }


}
