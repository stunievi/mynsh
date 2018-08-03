package com.beeasy.hzback.modules.system.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@ApiModel
@Data
public class WorkflowModelAdd {

    @ApiModelProperty(name = "name", value = "工作流名称",required = true)
    @NotEmpty(message = "名字不能为空")
    String name;

//    @ApiModelProperty(name = "version", value = "工作流版本", required = true)
//    @NotNull(message = "版本号不能为空")
//    BigDecimal version;

    @ApiModelProperty(name = "info", value = "工作流描述")
    String info;

    @NotNull(message = "处理周期必须为数字")
    @Range(min = 1, max = 99999, message = "处理周期在1-99999之间")
    Integer processCycle = 1;
}
