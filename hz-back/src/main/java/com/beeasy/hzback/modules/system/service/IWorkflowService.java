package com.beeasy.hzback.modules.system.service;

import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.system.entity.WorkflowInstance;
import com.beeasy.hzback.modules.system.form.WorkflowModelAdd;
import com.beeasy.hzback.modules.system.form.WorkflowQuartersEdit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IWorkflowService {
    public static enum Type {
        MAIN_QUARTERS(0),
        MAIN_USER(1),
        SUPPORT_QUARTERS(2),
        SUPPORT_USER(3);

        private int value = 0;

        private Type(int value){
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static enum Status{
        DOING,
        DID
    }

    public static enum NodeType{
        INPUT("input"),
        CHECK("check"),
        END("end");

        private String value;
        NodeType(String value){
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }


    /**
     * 开启一条新的工作流实例
     * @param user
     * @param modelId
     * @return
     */
    public boolean startNewInstance(User user, Integer modelId);

    public boolean submitData(User user, Long instanceId, Object data);
    public boolean changeOpen(Integer modelId, boolean open);
    public boolean setPersons(WorkflowQuartersEdit edit);
    public boolean deleteNode(Integer modelId, String[] nodeName);
    public boolean createWorkflow(String modelName, WorkflowModelAdd add) throws RestException;
    public boolean deleteWorkflow(Integer id);
    public boolean createNode(Integer modelId, String node);
    public Page<WorkflowInstance> getUserWorkflows(User user, Status status, Pageable pageable);

}
