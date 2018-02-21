package com.beeasy.hzback.lib.zed.metadata;


import com.beeasy.hzback.lib.zed.RolePermission;
import lombok.Data;

@Data
public class RoleMap {
    ICheckPermission checkPermission;
    IPermission permission;
    RolePermission rolePermission;
}
