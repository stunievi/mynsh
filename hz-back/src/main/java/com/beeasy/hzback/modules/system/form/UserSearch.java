package com.beeasy.hzback.modules.system.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

@ApiModel
@Data
public class UserSearch extends Pager {

    @ApiModelProperty(value = "账户/姓名")
    String name;

    @ApiModelProperty(value = "岗位ID")
    Set<Long> quarters;

    @ApiModelProperty(value = "是否禁用")
    Boolean baned;
}
