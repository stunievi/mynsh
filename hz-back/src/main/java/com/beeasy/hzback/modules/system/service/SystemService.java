package com.beeasy.hzback.modules.system.service;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.modules.system.dao.ISystemVariableDao;
import com.beeasy.hzback.modules.system.entity.SystemVariable;
import com.beeasy.hzback.modules.system.form.SystemVarEditRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.beeasy.hzback.modules.system.cache.SystemConfigCache.DEMO_CACHE_NAME;

@Service
@Transactional
public class SystemService {
    @Autowired
    ISystemVariableDao systemVariableDao;

    /**
     * 变量设置
     *
     * @param map
     * @return
     */
    public boolean set(Map<String, String> map) {
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
     *
     * @param keys
     * @return
     */
    public Map<String, String> get(String... keys) {
        return systemVariableDao.findAllByVarNameIn(Arrays.asList(keys))
                .stream()
                .collect(Collectors.toMap(SystemVariable::getVarName, SystemVariable::getVarValue));
    }

    /**
     * 变量删除
     *
     * @param key
     * @return
     */
    public boolean delete(String key) {
        return systemVariableDao.deleteByVarNameAndCanDeleteIsTrue(key) > 0;
    }


    @Cacheable(value = DEMO_CACHE_NAME, key = "'system_info'")
    public String getSystemInfo() {
        String tags = "java.version\n" +
                "java.vendor\n" +
                "java.vendor.url\n" +
                "java.home\n" +
                "java.vm.specification.version\n" +
                "java.vm.specification.vendor\n" +
                "java.vm.specification.name\n" +
                "java.vm.version\n" +
                "java.vm.vendor\n" +
                "java.vm.name\n" +
                "java.specification.version\n" +
                "java.specification.vendor\n" +
                "java.specification.name\n" +
                "java.class.version\n" +
                "java.class.path\n" +
                "java.library.path\n" +
                "java.io.tmpdir\n" +
                "java.compiler\n" +
                "java.ext.dirs\n" +
                "os.name\n" +
                "os.arch\n" +
                "os.version\n" +
                "file.separator\n" +
                "path.separator\n" +
                "line.separator\n" +
                "user.name\n" +
                "user.home\n" +
                "user.dir";
        return Result.ok(Arrays.stream(tags.split("\\n")).map(item -> {
            return new Object[]{item, System.getProperty(item)};
        }).collect(Collectors.toMap(item -> item[0], item -> String.valueOf(item[1])))).toJson();
    }
}
