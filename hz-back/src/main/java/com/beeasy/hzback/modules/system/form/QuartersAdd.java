package com.beeasy.hzback.modules.system.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class QuartersAdd {
    @ApiModelProperty(value = "岗位名称",required = true)
    @NotEmpty(message = "岗位名称不能为空")
    private String name;

    @ApiModelProperty(value = "岗位所属部门", required = true)
    @NotNull(message = "岗位所属部门不能为空")
    private Integer departmentId;

    @ApiModelProperty(value = "岗位描述")
    private String info;

}
