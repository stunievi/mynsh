package com.beeasy.hzback.modules.system.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class QuartersAdd {
    @ApiModelProperty(value = "岗位名称",required = true)
    @NotEmpty(message = "岗位名称不能为空")
    private String name;

    @ApiModelProperty(value = "岗位所属部门", required = true)
    @NotNull(message = "岗位所属部门不能为空")
    private Long departmentId;

    @ApiModelProperty(value = "岗位描述")
    private String info;

    @Range(min = 0,max = 255, message = "排序在0-255之间")
    int sort;

    boolean manager = false;
}
