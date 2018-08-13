package com.beeasy.hzback.modules.system.dao;

import com.beeasy.common.entity.WorkflowInstance;
import com.beeasy.common.entity.WorkflowInstanceObserver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IWorkflowInstanceObserverDao extends JpaRepository<WorkflowInstanceObserver,Long>{
}
