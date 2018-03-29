package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.WorkflowModel;
import com.beeasy.hzback.modules.system.entity.WorkflowModelPersons;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IWorkflowModelPersonsDao extends JpaRepository<WorkflowModelPersons,Integer> {
    void deleteAllByWorkflowModel(WorkflowModel workflowModel);

}
