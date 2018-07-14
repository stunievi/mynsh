package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.WorkflowNodeInstanceDealer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface IWorkflowNodeInstanceDealerDao extends JpaRepository<WorkflowNodeInstanceDealer,Long> {
    int countByTypeAndNodeInstanceIdAndUserId(WorkflowNodeInstanceDealer.Type type, final long nodeInstanceId, final long uid);
    int countByTypeInAndNodeInstanceIdAndUserId(Collection<WorkflowNodeInstanceDealer.Type> types, final long nodeInstanceId, final long uid);
    int countByTypeInAndNodeInstanceIdAndUserIdNot(Collection<WorkflowNodeInstanceDealer.Type> types, final long nodeInstanceId, final long uid);

    List<WorkflowNodeInstanceDealer> findAllByTypeAndNodeInstanceId(WorkflowNodeInstanceDealer.Type type, final long nodeInstanceId);
}
