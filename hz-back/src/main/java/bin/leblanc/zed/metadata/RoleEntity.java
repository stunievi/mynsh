package bin.leblanc.zed.metadata;

import bin.leblanc.zed.RolePermission;
import lombok.Data;

@Data
public class RoleEntity {
    private Object token;
    private RolePermission rolePermission;
}
