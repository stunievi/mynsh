package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.WorkflowInstanceAttribute;
import org.apache.commons.lang3.text.translate.NumericEntityUnescaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IWorkflowInstanceAttributeDao extends JpaRepository<WorkflowInstanceAttribute,Long> {
    Optional<WorkflowInstanceAttribute> findTopByInstanceIdAndAttrKey(long instanceId, String attrKey);
    List<WorkflowInstanceAttribute> findAllByInstanceIdAndAndAttrKeyIn(long instanceId, List<String> fields);


    //删除字段
    int deleteByInstanceIdAndAttrKey(long instanceId, String attrKey);
}
