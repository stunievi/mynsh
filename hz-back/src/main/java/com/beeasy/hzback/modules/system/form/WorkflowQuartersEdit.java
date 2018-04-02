package com.beeasy.hzback.modules.system.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class WorkflowQuartersEdit {
    @NotNull(message = "模型ID不能为空")
    @ApiModelProperty(value = "工作流模型ID", required = true)
    Integer modelId;

    @NotEmpty(message = "节点名不能为空")
    @ApiModelProperty(value = "节点名字", required = true)
    String name;

    @ApiModelProperty(value = "主办岗位")
    Long[] mainQuarters;

    @ApiModelProperty(value = "主办用户")
    Long[] supportQuarters;

    @ApiModelProperty(value = "协办岗位")
    Long[] mainUser;

    @ApiModelProperty(value = "协办用户")
    Long[] supportUser;
}
