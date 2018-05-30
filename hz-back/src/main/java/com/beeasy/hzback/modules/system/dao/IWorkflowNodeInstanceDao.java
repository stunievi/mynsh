package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.WorkflowInstance;
import com.beeasy.hzback.modules.system.entity.WorkflowModel;
import com.beeasy.hzback.modules.system.entity.WorkflowNodeInstance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface IWorkflowNodeInstanceDao extends JpaRepository<WorkflowNodeInstance,Long> {

    @Query(
            value = "select node.instance from WorkflowNodeInstance node join node.instance instance where instance.workflowModel in :models and node.nodeName in :names and node.finished = :finished group by node.instance",
            countQuery = "select count(node) from WorkflowNodeInstance node join node.instance instance where instance.workflowModel in :models and node.nodeName in :names and node.finished = :finished group by node.instance")
    Page<WorkflowInstance> getInstanceList(@Param("models") Set<WorkflowModel> models, @Param("names") Set<String> names, @Param("finished") boolean finished, Pageable pageable);


    @Query("select n from WorkflowNodeInstance n where n.nodeModel.type = :type and n.finished = false")
    Page<WorkflowNodeInstance> getCurrentNode(@Param("type") String type, Pageable pageable);


    Optional<WorkflowNodeInstance> findFirstByInstanceAndFinishedIsFalse(WorkflowInstance instance);
}
