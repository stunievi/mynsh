package com.beeasy.rpc;

import com.beeasy.common.entity.WorkflowInstance;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.modules.mobile.request.ApplyTaskRequest;
import com.beeasy.hzback.modules.system.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.Date;

public class DubboServiceImpl implements DubboService {

    @Lazy
    @Autowired
    WorkflowService workflowService;

    @Override
    public String autoStartTask(String dataSource, String dataId, String modelName, long dealerId, Date date) {
        ApplyTaskRequest request = new ApplyTaskRequest();
        request.setDataSource(ApplyTaskRequest.DataSource.ACC_LOAN);
        request.setDealerId(dealerId);
        request.setManual(false);
        request.setDataId(dataId);
        Result result = workflowService.autoStartTask(dealerId, request);
        if(result.isSuccess()){
            return "";
        }
        return result.getErrMessage();
    }
}
