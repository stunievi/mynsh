package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.WorkflowNodeInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IWorkflowNodeInstanceDao extends JpaRepository<WorkflowNodeInstance,Long> {
}
