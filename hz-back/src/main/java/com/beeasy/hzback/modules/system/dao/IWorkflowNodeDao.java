package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.WorkflowNode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IWorkflowNodeDao extends JpaRepository<WorkflowNode,Long>{
}
