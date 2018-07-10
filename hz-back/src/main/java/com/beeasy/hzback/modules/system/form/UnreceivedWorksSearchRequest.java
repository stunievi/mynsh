package com.beeasy.hzback.modules.system.form;

import com.beeasy.hzback.modules.system.entity.WorkflowInstance;
import lombok.Data;

@Data
public class UnreceivedWorksSearchRequest {

    //任务编号
    String id;

    //客户经理编号
    Long userId;

    //任务类型
    String type;

    //任务状态
    WorkflowInstance.State state;

    String modelName;
}
