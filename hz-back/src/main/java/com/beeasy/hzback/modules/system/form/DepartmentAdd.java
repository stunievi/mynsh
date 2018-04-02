package com.beeasy.hzback.modules.system.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class DepartmentAdd {

    @ApiModelProperty(value = "部门名称",required = true)
    @NotEmpty(message = "部门名称不能为空")
    private String name;

    @ApiModelProperty(value = "部门父级ID,顶级部门请写0",required = true)
    @Min(value = 0,message = "父级ID不能小于0")
    @NotNull(message = "父级ID不能为空")
    private Long parentId;

    @ApiModelProperty(value = "部门描述")
    private String info;
}
