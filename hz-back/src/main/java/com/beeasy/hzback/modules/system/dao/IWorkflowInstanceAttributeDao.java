package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.WorkflowInstanceAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IWorkflowInstanceAttributeDao extends JpaRepository<WorkflowInstanceAttribute,Long> {

}
