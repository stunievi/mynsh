package com.beeasy.hzback.modules.system.dao;

import com.beeasy.common.entity.WorkflowInstanceTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IWorkflowInstanceTransactionDao extends JpaRepository<WorkflowInstanceTransaction,Long>{

    List<WorkflowInstanceTransaction> findAllByUserIdAndInstanceIdInAndStateIn(long uid, Collection<Long> ids, Collection<WorkflowInstanceTransaction.State> states);
}
