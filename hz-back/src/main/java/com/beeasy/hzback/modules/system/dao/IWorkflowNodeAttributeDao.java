package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.WorkflowNodeAttribute;
import com.beeasy.hzback.modules.system.entity.WorkflowNodeInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IWorkflowNodeAttributeDao extends JpaRepository<WorkflowNodeAttribute,Long> {
    Optional<WorkflowNodeAttribute> findFirstByNodeInstanceAndAttrKey(WorkflowNodeInstance instance, String attrKey);
}
