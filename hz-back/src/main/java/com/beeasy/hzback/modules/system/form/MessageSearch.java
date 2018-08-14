package com.beeasy.hzback.modules.system.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class MessageSearch extends Pager {
    @ApiModelProperty(value = "是否已读", notes = "不传为查找全部")
    Boolean read;
}
