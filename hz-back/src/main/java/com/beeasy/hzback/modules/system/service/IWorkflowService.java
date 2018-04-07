package com.beeasy.hzback.modules.system.service;

import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.modules.exception.CannotFindEntityException;
import com.beeasy.hzback.modules.system.entity.InspectTask;
import com.beeasy.hzback.modules.system.entity.WorkflowInstance;
import com.beeasy.hzback.modules.system.entity.WorkflowModel;
import com.beeasy.hzback.modules.system.form.WorkflowModelAdd;
import com.beeasy.hzback.modules.system.form.WorkflowQuartersEdit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.Optional;

public interface IWorkflowService {
    WorkflowModel findModelE(long id) throws CannotFindEntityException;

    Optional<WorkflowInstance> findInstance(long id);

    WorkflowInstance findInstanceE(long id) throws CannotFindEntityException;

    Optional<InspectTask> findInspectTask(long id);

    InspectTask findInspectTaskE(long id) throws CannotFindEntityException;

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

    enum InspectTaskType{
        AUTO, MANUAL
    }
    enum InspectTaskState{
        //已生成
        CREATED,
        //已被领取
        RECEIVED,
        //处理完毕
        FINISHED
    }

    /**
     * 开启一条新的工作流实例
     * @param uid
     * @param modelId
     * @return
     */
    WorkflowInstance startNewInstance(long uid, long modelId) throws RestException;

    /**
     * 如果是资料节点, 应该是键值对map
     * 如果是审核节点, 则只需要提交一个处理结果(通常为string)
     * @param user
     * @param instanceId
     * @param data
     * @param ps 审核说明, 可写可不写
     * @return
     */
    WorkflowInstance submitData(long uid, long instanceId, Map data) throws RestException;
    /**
     * 一经启用, 禁止再编辑
     * @param modelId
     * @param open
     * @return
     */
    WorkflowModel setOpen(long modelId, boolean open) throws CannotFindEntityException;

    WorkflowModel setPersons(long modelId, WorkflowQuartersEdit... edits);

    public WorkflowModel deleteNode(long modelId, String[] nodeName) throws CannotFindEntityException;
    WorkflowModel createWorkflow(String modelName, WorkflowModelAdd add) throws RestException;

    boolean deleteWorkflowModel(long id, boolean force);

    public boolean createNode(long modelId, String node);
    Page<WorkflowInstance> getUserWorkflows(long uid, Status status, Pageable pageable) throws CannotFindEntityException;


    /**
     * 只允许在资料节点前进下一步, 审核节点的判定应该在每次节点提交的时候
     * @param
     * @param instanceId
     * @return
     */
    WorkflowInstance goNext(long uid, long instanceId) throws RestException;

    /**
     * 创建一条新的检查任务
     * @param modelName
     * @param userId
     * @param isAuto
     * @return
     */
    InspectTask createInspectTask(String modelName, long userId, boolean isAuto);

    /**
     * 接受检查任务, 创建一条新的工作流任务
     * @param userId
     * @param taskId
     * @return
     * @throws RestException
     */
    WorkflowInstance acceptInspectTask(long userId, long taskId) throws RestException;


    Optional<WorkflowModel> findModel(long id);

}
