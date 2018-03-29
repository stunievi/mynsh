package com.beeasy.hzback.modules.system.form;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class WorkflowQuartersEdit {
    @NotNull(message = "模型ID不能为空")
    @ApiModelProperty(value = "工作流模型ID", required = true)
    Integer modelId;

    @ApiModelProperty(value = "主办岗位")
    Integer[] mainQuarters;

    @ApiModelProperty(value = "主办用户")
    Integer[] supportQuarters;

    @ApiModelProperty(value = "协办岗位")
    Integer[] mainUser;

    @ApiModelProperty(value = "协办用户")
    Integer[] supportUser;
}
