package com.beeasy.hzback.modules.mobile.request;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class WorkflowPositionAddRequest {
    @NotNull(message = "任务ID不能为空")
    Long instanceId;

    @NotNull(message = "节点ID不能为空")
    Long nodeInstanceId;

    @NotEmpty(message = "地址不能为空")
    String position;

    @NotNull(message = "经度不能为空")
    double lat;

    @NotNull(message = "纬度不能为空")
    double lng;
}
