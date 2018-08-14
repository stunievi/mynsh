package com.beeasy.hzback.modules.mobile.request;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class SubmitDataRequest {

    @NotNull(message = "任务ID不能为空")
    Long instanceId;

    @NotNull(message = "提交的节点ID不能为空")
    Long nodeId;

    @NotNull(message = "提交的数据不能为空")
    JSONObject data;
}
