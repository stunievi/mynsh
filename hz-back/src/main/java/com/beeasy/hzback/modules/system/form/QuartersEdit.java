package com.beeasy.hzback.modules.system.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@ApiModel
@Data
public class QuartersEdit {
    @ApiModelProperty(value = "岗位ID", required = true)
    Long id;

    @ApiModelProperty(value = "岗位名称")
    private String name;

    @ApiModelProperty(value = "岗位描述")
    private String info;

    //是否主管
    private Boolean manager = null;

    @Range(min = 0,max = 255, message = "排序在0-255之间")
    int sort;
}
