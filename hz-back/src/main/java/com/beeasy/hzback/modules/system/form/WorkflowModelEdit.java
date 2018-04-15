package com.beeasy.hzback.modules.system.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowModelEdit {

    @NotNull(message = "模型ID不能为空")
    @ApiModelProperty(name = "id", value = "工作流模型ID")
    Long id;

    @ApiModelProperty(name = "info", value = "工作流描述")
    String info;

    @ApiModelProperty(name = "open",value = "是否打开工作流")
    Boolean open;

}
