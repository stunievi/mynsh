package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.WorkflowNodeFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IWorkflowNodeFileDao extends JpaRepository<WorkflowNodeFile,Long>{
}
