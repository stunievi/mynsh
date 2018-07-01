package com.beeasy.hzback.modules.system.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ApiModel
@Data
public class DepartmentEdit {

    @ApiModelProperty(value = "部门ID")
    @NotNull(message = "部门ID不能为空")
    Long id;

    @ApiModelProperty(value = "部门名称")
    private String name;

    @ApiModelProperty(value = "部门父级ID,顶级部门请写0")
    @Min(value = 0,message = "父级ID不能小于0")
    private Long parentId;

    @ApiModelProperty(value = "部门描述")
    private String info;

    @Range(min = 0,max = 255, message = "排序在0-255之间")
    int sort;

    String accCode = "";
}
