package com.beeasy.hzback.lib.zed;

import java.util.HashMap;
import java.util.Map;

public class RolePermission {


    /**
     * 全局权限注册
     */
    private Map<String,Boolean> allowMap = new HashMap<>();
    private Map<String,Boolean> disallowMap = new HashMap<>();


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
}
