package com.beeasy.hzback.modules.system.node;

import com.beeasy.common.helper.AbstractBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;

@ApiModel
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckNodeState extends AbstractBaseEntity {

    @ApiModelProperty(required = true)
    @NotEmpty(message = "状态选项不能为空")
    private String item;

    @ApiModelProperty(required = true)
    @Min(value = 1,message = "状态满足条件格式错误")
    private int condition;

    @ApiModelProperty(required = true)
    @NotEmpty(message = "状态触发行为不能为空")
    private String behavior;
}
