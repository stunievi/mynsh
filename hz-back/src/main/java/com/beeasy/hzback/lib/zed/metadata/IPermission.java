package com.beeasy.hzback.lib.zed.metadata;

import com.beeasy.hzback.lib.zed.RolePermission;

public interface IPermission {
    void call(RolePermission role);
}
