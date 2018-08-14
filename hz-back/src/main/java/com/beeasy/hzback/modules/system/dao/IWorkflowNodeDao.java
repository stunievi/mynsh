package com.beeasy.hzback.modules.system.dao;

import com.beeasy.common.entity.WorkflowModel;
import com.beeasy.common.entity.WorkflowNode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IWorkflowNodeDao extends JpaRepository<WorkflowNode, Long> {
    Optional<WorkflowNode> findFirstByModelAndName(WorkflowModel model, String name);

    Optional<WorkflowNode> findTopByModelIdAndName(long modelId, String name);

    List<WorkflowNode> findAllByModelAndEndIsTrue(WorkflowModel model);

    List<WorkflowNode> findAllByModelAndStartIsTrue(WorkflowModel model);

    List<WorkflowNode> findAllByModelIdInAndStartIsTrue(final Collection<Long> modelIds);

    Optional<WorkflowNode> findFirstByModel_IdAndStartIsTrue(long modelId);

    void deleteAllByModel_IdAndIdAndStartIsFalseAndEndIsFalse(long modelId, long nodeId);
}
