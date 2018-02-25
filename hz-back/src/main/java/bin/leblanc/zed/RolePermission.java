package bin.leblanc.zed;

import bin.leblanc.zed.metadata.ICheckPermission;
import bin.leblanc.zed.metadata.IPermission;
import lombok.Data;

import java.util.*;

@Data
public class RolePermission {

    public final static String SUPERUSER = "SU";
    public final static String UNKNOWN = "UNKNOWN";


    /**
     * 全局权限注册
     */
    private Map<String,Boolean> allowMap = new HashMap<>();
    private Map<String,Boolean> disallowMap = new HashMap<>();

//    private ICheckPermission checkPermission;

    private IPermission permission;

    private String roleName;

    private Map<Class,EntityPermission> entityPermissionMap = new HashMap();

    public void allowAllGet(){
        disallowMap.remove(Zed.GET);
        allowMap.put(Zed.GET,true);
    }

    public void allowAllPost(){
        disallowMap.remove(Zed.POST);
        allowMap.put(Zed.POST,true);
    }

    public void allowAllPut(){
        disallowMap.remove(Zed.PUT);
        allowMap.put(Zed.PUT,true);
    }

    public void allowAllDelete(){
        disallowMap.remove(Zed.DELETE);
        allowMap.put(Zed.DELETE,true);
    }

    public void disallowAllGet(){
        allowMap.remove(Zed.GET);
        disallowMap.put(Zed.GET,true);
    }

    public void disallowAllPost(){
        allowMap.remove(Zed.POST);
        disallowMap.put(Zed.POST,true);
    }

    public void disallowAllPut(){
        allowMap.remove(Zed.PUT);
        disallowMap.put(Zed.PUT,true);
    }

    public void disallowAllDelete(){
        allowMap.remove(Zed.DELETE);
        disallowMap.put(Zed.DELETE,true);
    }


    public EntityPermission createEntityPermission(Class clz){
        EntityPermission entityPermission = new EntityPermission(clz);
        entityPermissionMap.put(clz,entityPermission);
        return entityPermission;
    }

    public Optional<EntityPermission> getEntityPermission(Class clz){
        return Optional.ofNullable(entityPermissionMap.get(clz));
    }
}
