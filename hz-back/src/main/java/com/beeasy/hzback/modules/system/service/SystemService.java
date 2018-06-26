package com.beeasy.hzback.modules.system.service;

import com.beeasy.hzback.modules.system.dao.ISystemVariableDao;
import com.beeasy.hzback.modules.system.entity.SystemVariable;
import com.beeasy.hzback.modules.system.form.SystemVarEditRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
            systemVariableDao.save(systemVariable);
        }
        return true;
    }


    /**
     * 变量获取
     * @param keys
     * @return
     */
    public Map<String,String> get(String ...keys){
        return systemVariableDao.findAllByVarNameIn(Arrays.asList(keys))
                .stream()
                .collect(Collectors.toMap(SystemVariable::getVarName,SystemVariable::getVarValue));
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
