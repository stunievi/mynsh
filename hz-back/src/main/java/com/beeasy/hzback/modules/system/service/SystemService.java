package com.beeasy.hzback.modules.system.service;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.modules.system.dao.IMessageDao;
import com.beeasy.hzback.modules.system.dao.IMessageTemplateDao;
import com.beeasy.hzback.modules.system.dao.ISystemVariableDao;
import com.beeasy.hzback.modules.system.entity.Message;
import com.beeasy.hzback.modules.system.entity.MessageTemplate;
import com.beeasy.hzback.modules.system.entity.SystemVariable;
import com.beeasy.hzback.modules.system.form.SystemVarEditRequest;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

import static com.beeasy.hzback.modules.system.cache.SystemConfigCache.DEMO_CACHE_NAME;

@Service
@Transactional
public class SystemService {
    @Autowired
    ISystemVariableDao systemVariableDao;
    @Autowired
    IMessageTemplateDao messageTemplateDao;

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
            systemVariable.setCanDelete(true);
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
     * @param keys
     * @return
     */
    public boolean delete(Collection<String> keys) {
        for (String key : keys) {
           int count = systemVariableDao.deleteByVarNameAndCanDeleteIsTrue(key);
           if(count == 0){
               throw new RuntimeException();
           }
        }
        return true;
    }


    public MessageTemplate addMessageTemplate(String template){
        MessageTemplate messageTemplate = new MessageTemplate();
        messageTemplate.setTemplate(template);
        return messageTemplateDao.save(messageTemplate);
    }

    public boolean editMessageTemplate(MessageTemplateRequest request){
        MessageTemplate messageTemplate = messageTemplateDao.findTopById(request.getId()).orElse(null);
        if(null == messageTemplate){
            return false;
        }
        messageTemplate.setTemplate(request.getTemplate());
        messageTemplateDao.save(messageTemplate);
        return true;
    }

    public List<Long> deleteMessageTemplates(Collection<Long> ids ){
        return ids.stream().filter(id -> messageTemplateDao.deleteById(id) > 0).collect(Collectors.toList());
    }

    public Page<MessageTemplate> getMessageTemplateList(Pageable pageable){
        return messageTemplateDao.findAll(pageable);
    }

    public Optional<MessageTemplate> getMessageTemplateById(long id){
        return messageTemplateDao.findTopById(id);
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


    @Data
    public static class  MessageTemplateRequest{
        public interface add{};
        public interface edit{};

        @NotNull(groups = add.class)
        Long id;

        @NotEmpty(groups = {add.class,edit.class})
        String template;
    }
}
