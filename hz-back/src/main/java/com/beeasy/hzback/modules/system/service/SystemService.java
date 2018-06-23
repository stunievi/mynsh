package com.beeasy.hzback.modules.system.service;

import com.beeasy.hzback.modules.system.dao.ISystemVariableDao;
import com.beeasy.hzback.modules.system.entity.SystemVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SystemService {
    @Autowired
    ISystemVariableDao systemVariableDao;

    /**
     * 变量设置
     * @param key
     * @param value
     * @param canDelete
     * @return
     */
    public boolean set(String key, String value, boolean canDelete){
        SystemVariable systemVariable = systemVariableDao.findFirstByVarName(key).orElse(new SystemVariable());
        systemVariable.setVarName(key);
        systemVariable.setVarValue(value);
        systemVariable.setCanDelete(canDelete);
        return true;
    }


    /**
     * 变量获取
     * @param key
     * @return
     */
    public String get(String key){
        SystemVariable systemVariable = systemVariableDao.findFirstByVarName(key).orElse(null);
        if(null != systemVariable){
            return systemVariable.getVarValue();
        }
        return "";
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
