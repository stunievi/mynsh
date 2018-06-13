package com.beeasy.hzback.modules.system.response;


import com.beeasy.hzback.modules.system.entity.SystemTextLog;
import com.beeasy.hzback.modules.system.entity.WorkflowInstance;
import com.beeasy.hzback.modules.system.entity.WorkflowNode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FetchWorkflowInstanceResponse {

    //任务主体
    WorkflowInstance instance;

    //任务变更日志
    @Deprecated
    List<SystemTextLog> logs = new ArrayList<>();

    //是否可以处理
    boolean deal = false;

    //是否可以取消
    boolean cancel = false;

    //是否可以撤回
    boolean recall = false;

    //是否可以移交
    boolean transform = false;

    //是否可以接受
    boolean accept = false;

    //可移交对象
    List<Long> transformUsers = new ArrayList<>();

    //当前节点模型
    WorkflowNode currentNodeModel;

}
