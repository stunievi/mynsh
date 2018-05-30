package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.WorkflowModelPersons;
import com.beeasy.hzback.modules.system.entity.WorkflowNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface IWorkflowModelPersonsDao extends JpaRepository<WorkflowModelPersons,Long> {

    @Query("select s from WorkflowModelPersons s where (s.type = 0 and s.uid in :quartersIds) or (s.type = 1 and s.uid in :userIds)")
    Set<WorkflowModelPersons> findPersonsByUser(@Param("quartersIds") List<Long> quartersIds, @Param("userIds") List<Long> userIds);

    void deleteAllByWorkflowNode(WorkflowNode node);
}
