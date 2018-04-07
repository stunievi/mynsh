package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.WorkflowModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface IWorkflowModelDao extends JpaRepository<WorkflowModel,Long>{
    WorkflowModel findFirstByNameAndVersion(String name, BigDecimal version);
    Page<List<WorkflowModel>> findAllByName(String name, Pageable pageable);

    @Query("select m from WorkflowModel m where m.baseName = :modelName and m.open = true order by m.version desc")
    List<WorkflowModel> findRecentVersions(String modelName);
}
