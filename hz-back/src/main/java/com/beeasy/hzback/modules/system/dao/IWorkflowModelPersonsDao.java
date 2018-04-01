package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.WorkflowModel;
import com.beeasy.hzback.modules.system.entity.WorkflowModelPersons;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface IWorkflowModelPersonsDao extends JpaRepository<WorkflowModelPersons,Integer> {
    void deleteAllByWorkflowModel(WorkflowModel workflowModel);

    @Query("select s from WorkflowModelPersons s where (s.type = 0 and s.uid in :quartersIds) or (s.type = 1 and s.uid in :userIds)")
    Set<WorkflowModelPersons> findPersonsByUser(List<Integer> quartersIds, List<Integer> userIds);
}
