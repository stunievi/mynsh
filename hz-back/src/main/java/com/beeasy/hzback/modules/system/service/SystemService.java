package com.beeasy.hzback.modules.system.service;

import com.beeasy.hzback.modules.system.dao.ISystemVariableDao;
import com.beeasy.hzback.modules.system.entity.SystemVariable;
import com.beeasy.hzback.modules.system.form.SystemVarEditRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class SystemService {
    @Autowired
    ISystemVariableDao systemVariableDao;

    /**
     * 变量设置
     * @param map
     * @return
     */
    public boolean set(Map<String,String> map){
        for (Map.Entry<String, String> entry : map.entrySet()) {
            SystemVariable systemVariable = systemVariableDao.findFirstByVarName(entry.getKey()).orElse(new SystemVariable());
            systemVariable.setVarName(entry.getKey());
            systemVariable.setVarValue(entry.getValue());
            systemVariable.setCanDelete(false);
        }
        return true;
    }


    /**
     * 变量获取
     * @param keys
     * @return
     */
    public Map<String,String> get(String ...keys){
        Map<String,String> map = new HashMap<>();
        for (String key : keys) {
            SystemVariable systemVariable = systemVariableDao.findFirstByVarName(key).orElse(null);
            if(null != systemVariable){
                map.put(key, systemVariable.getVarValue());
                continue;
            }
            map.put(key,"");
        }
        return map;
    }

    /**
     * 变量删除
     * @param key
     * @return
     */
    public boolean delete(String key){
        return systemVariableDao.deleteByVarNameAndCanDeleteIsTrue(key) > 0;
    }
}
