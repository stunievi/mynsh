package com.beeasy.hzback.modules.system.form;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.modules.system.entity.GlobalPermission;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class GlobalPermissionEditRequest {

    @NotNull
    GlobalPermission.Type type;

    @NotNull
    GlobalPermission.UserType userType;

    @NotNull
    @Size(min = 1)
    List<Long> linkIds;

    @NotNull
    Long objectId;

    JSONObject object;
    JSONArray array;
}
