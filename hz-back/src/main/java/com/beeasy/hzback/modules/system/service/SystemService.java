package com.beeasy.hzback.modules.system.service;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.SpringContextUtils;
import com.beeasy.hzback.modules.system.dao.IMessageDao;
import com.beeasy.hzback.modules.system.dao.IMessageTemplateDao;
import com.beeasy.hzback.modules.system.dao.IShortMessageLogDao;
import com.beeasy.hzback.modules.system.dao.ISystemVariableDao;
import com.beeasy.hzback.modules.system.entity.Message;
import com.beeasy.hzback.modules.system.entity.MessageTemplate;
import com.beeasy.hzback.modules.system.entity.SystemVariable;
import com.beeasy.hzback.modules.system.form.SystemVarEditRequest;
import io.netty.util.internal.StringUtil;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.reflections.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Id;
import javax.persistence.criteria.Predicate;
import javax.validation.constraints.*;
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
    @Autowired
    IShortMessageLogDao shortMessageLogDao;

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
        }
        return true;
    }


    public MessageTemplate addMessageTemplate(MessageTemplateRequest request){
        MessageTemplate messageTemplate = new MessageTemplate();
        messageTemplate.setName(request.getName());
        messageTemplate.setTemplate(request.getTemplate());
        messageTemplate.setPlaceholder(request.getPlaceholder());
        return messageTemplateDao.save(messageTemplate);
    }

    public boolean editMessageTemplate(MessageTemplateRequest request){
        MessageTemplate messageTemplate = messageTemplateDao.findById(request.getId()).orElse(null);
        if(null == messageTemplate){
            return false;
        }
        messageTemplate.setName(request.getName());
        messageTemplate.setTemplate(request.getTemplate());
        messageTemplate.setPlaceholder(request.getPlaceholder());
        messageTemplateDao.save(messageTemplate);
        return true;
    }

    public List<Long> deleteMessageTemplates(Collection<Long> ids ){
        return ids.stream().peek(id -> messageTemplateDao.deleteById(id)).collect(Collectors.toList());
    }

    public Page<MessageTemplate> getMessageTemplateList(MessageTemplateSearchRequest request, Pageable pageable){
        Specification query = ((root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(!Utils.isEmpty(request.getName())){
                predicates.add(cb.like(root.get("name"),"%" + request.getName() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        });
        return messageTemplateDao.findAll(query,pageable);
    }

    public Optional<MessageTemplate> getMessageTemplateById(long id){
        return messageTemplateDao.findById(id);
    }


    /**
     * 短信历史检索
     * @param request
     * @param pageable
     * @return
     */
    public Page getShortMessageLog(ShortMessageSearchRequest request, Pageable pageable){
        Specification query = ((root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(!StringUtils.isEmpty(request.getPhone())){
                predicates.add(
                        cb.like(root.get("phone"),"%" + request.getPhone() + "%")
                );
            }
            if(!StringUtils.isEmpty(request.getKeyword())){
                predicates.add(cb.like(root.get("message"), "%" + request.getKeyword() + "%"));
            }
            if(null != request.getStartDate()){
                predicates.add(cb.greaterThan(root.get("addTime"), new Date(request.getStartDate())));
            }
            if(null != request.getEndDate()){
                predicates.add(cb.lessThan(root.get("addTime"), new Date(request.getEndDate())));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        });
        return shortMessageLogDao.findAll(query,pageable);
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
    public static class MessageTemplateRequest{
        public interface add{};
        public interface edit{};

        @NotNull(groups = add.class)
        Long id;

        @NotBlank(message = "模板名不能为空", groups = {add.class,edit.class})
        @Size(min = 2,max = 20,message = "模板名长度在2-20之间",groups = {add.class,edit.class})
        String name;

        @NotBlank(message = "模板内容不能为空",groups = {add.class,edit.class})
        @Size(min = 5,max = 200, message = "模板内容长度在5-200之间", groups = {add.class,edit.class})
        String template;

        @NotBlank(message = "查询语句不能为空", groups = {add.class,edit.class})
        String placeholder;

        //验证不同命
        @AssertTrue(message = "已经有同名模板", groups = {add.class,edit.class})
        public boolean getCheckName(){
            if(null == id){
                return SpringContextUtils.getBean(IMessageTemplateDao.class).countByName(name) == 0;
            }
            else{
                return SpringContextUtils.getBean(IMessageTemplateDao.class).countByNameAndIdNot(name,id) == 0;
            }
        }
    }

    @Data
    public static class MessageTemplateSearchRequest{
        String name;
    }

    @Data
    public static class ShortMessageSearchRequest{
        String phone;
        String keyword;
        Long startDate;
        Long endDate;
    }
}
