package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.WorkflowInstanceTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IWorkflowInstanceTransactionDao extends JpaRepository<WorkflowInstanceTransaction,Long>{
}
