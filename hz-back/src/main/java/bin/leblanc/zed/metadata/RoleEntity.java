package bin.leblanc.zed.metadata;

import bin.leblanc.zed.RolePermission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleEntity {
    private Object token;
    private RolePermission rolePermission;
}
