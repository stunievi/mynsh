package com.beeasy.hzback.modules.system.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@ApiModel
@Data
public class WorkflowQuartersEdit {
    @NotNull(message = "节点ID不能为空")
    @ApiModelProperty(value = "节点ID", required = true)
    Long nodeId;

//    @NotEmpty(message = "节点名不能为空")
    //废弃字段
    //节点名字已经不重要了, 因为有ID做保证
    @ApiModelProperty(value = "节点名字", required = true)
    String name;

    @ApiModelProperty(value = "主办岗位")
    Set<Long> mainQuarters = new HashSet<>();

    @ApiModelProperty(value = "协办岗位")
    Set<Long> supportQuarters = new HashSet<>();

//    @ApiModelProperty(value = "协办岗位")
//    Set<Long> mainUser = new HashSet<>();

//    @ApiModelProperty(value = "协办用户")
//    Set<Long> supportUser = new HashSet<>();
}
