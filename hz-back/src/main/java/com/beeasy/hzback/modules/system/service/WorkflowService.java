package com.beeasy.hzback.modules.system.service;

import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.system.dao.IWorkflowInstanceDao;
import com.beeasy.hzback.modules.system.dao.IWorkflowModelDao;
import com.beeasy.hzback.modules.system.dao.IWorkflowNodeInstanceDao;
import com.beeasy.hzback.modules.system.entity.WorkflowInstance;
import com.beeasy.hzback.modules.system.entity.WorkflowModel;
import com.beeasy.hzback.modules.system.entity.WorkflowModelPersons;
import com.beeasy.hzback.modules.system.entity.WorkflowNodeInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class WorkflowService {

    @Autowired
    IWorkflowModelDao workflowModelDao;

    @Autowired
    IWorkflowInstanceDao workflowInstanceDao;

    @Autowired
    IWorkflowNodeInstanceDao workflowNodeInstanceDao;


    /**
     * 开始一个新的工作流任务
     * @param user
     * @param modelId
     * @return
     */
    public boolean startNewTask(User user, Integer modelId){
        if(modelId == null) return false;
        WorkflowModel workflowModel = workflowModelDao.findOne(modelId);
        if(workflowModel == null || !workflowModel.isOpen()){
            return false;
        }
        List<WorkflowModelPersons> persons = workflowModel.getPersons();
        if(!persons
                .stream()
                .anyMatch(p -> {
                    return
                            //该人符合个人用户的条件
                            (p.getType().equals(WorkflowModelPersons.Type.MAIN_USER) && p.getUid() == user.getId())
                                    ||
                                    //该人所属的岗位符合条件
                                    (p.getType().equals(WorkflowModelPersons.Type.MAIN_QUARTERS) && user.hasQuarters(p.getUid()));
                } )){

            return false;
        }

        //任务主体
        WorkflowInstance workflowInstance = new WorkflowInstance();
        workflowInstance.setWorkflowModel(workflowModel);
        workflowInstance = workflowInstanceDao.save(workflowInstance);

        //插入第一个节点
        String firstName = getFirstNodeName(workflowModel);
        if(firstName == null) return false;
        WorkflowNodeInstance workflowNodeInstance = new WorkflowNodeInstance();
        workflowNodeInstance.setNodeName(firstName);
        workflowNodeInstance.setWorkflowInstance(workflowInstance);
        workflowNodeInstance.setFinished(false);
        workflowNodeInstanceDao.save(workflowNodeInstance);

        return true;
    }

    public boolean submitData(){
        return false;
    }


    private String getFirstNodeName(WorkflowModel workflowModel){
        for (Map.Entry<String, Map> entry : workflowModel.getModel().entrySet()) {
            if(entry.getValue().containsKey("start")){
                return entry.getKey();
            }
        }
        return null;
    }

    private String getLastNodeName(WorkflowModel workflowModel){
        for (Map.Entry<String, Map> entry : workflowModel.getModel().entrySet()) {
            if(entry.getValue().containsKey("end")){
                return entry.getKey();
            }
        }
        return null;
    }

}
