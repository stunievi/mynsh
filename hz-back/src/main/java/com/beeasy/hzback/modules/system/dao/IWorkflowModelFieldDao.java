package com.beeasy.hzback.modules.system.dao;

import com.beeasy.common.entity.WorkflowModelField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IWorkflowModelFieldDao extends JpaRepository<WorkflowModelField, Long> {
    Optional<WorkflowModelField> findTopByModelIdAndName(long mid, String name);

    List<WorkflowModelField> findAllByModelId(long mid);

    int deleteAllByModelIdAndNameNotIn(long mid, Collection<String> names);

    int deleteAllByModelId(long mid);
}
