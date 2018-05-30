package com.beeasy.hzback.modules.system.form;

import com.beeasy.hzback.modules.system.node.CheckNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;

@ApiModel
@Data
public class CheckNodeModel extends CheckNode{

    @ApiModelProperty(value = "模型ID",required = true)
    @NotNull(message = "模型ID不能为空")
    Long modelId;

    Long nodeId;

    @ApiModelProperty(hidden = true)
    String type = "check";

    @ApiModelProperty(required = true)
    ArrayList<String> next = new ArrayList<>();

    @ApiModelProperty
    @NotEmpty(message = "节点名不能为空")
    String name;
}
