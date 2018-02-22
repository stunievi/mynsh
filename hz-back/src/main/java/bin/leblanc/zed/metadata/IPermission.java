package bin.leblanc.zed.metadata;

import bin.leblanc.zed.RolePermission;

public interface IPermission {
    void call(RolePermission role);
}
