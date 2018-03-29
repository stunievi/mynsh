package com.beeasy.hzback.modules.system.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.domain.Pageable;

@ApiModel
@Data
public class Pager {
    @ApiModelProperty(name = "page",value = "页码", notes = "默认为1")
    private Integer page;
    @ApiModelProperty(name = "size",value = "每页条数", notes = "默认为15")
    private Integer size;
    @ApiModelProperty(name = "sort",value = "排序", notes = "默认为主键倒序排列")
    private String sort;
}
