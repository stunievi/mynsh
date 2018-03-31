package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.WorkflowInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IWorkflowInstanceDao extends JpaRepository<WorkflowInstance,Long>{
}
