package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.WorkflowInstance;
import com.beeasy.hzback.modules.system.entity.WorkflowInstanceObserver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IWorkflowInstanceObserverDao extends JpaRepository<WorkflowInstanceObserver,Long>{
}
