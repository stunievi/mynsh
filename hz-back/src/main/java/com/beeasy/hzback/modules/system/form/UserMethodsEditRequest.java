package com.beeasy.hzback.modules.system.form;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserMethodsEditRequest {
    @NotNull
    long id;

    @NotNull
    JSONArray array;
}
