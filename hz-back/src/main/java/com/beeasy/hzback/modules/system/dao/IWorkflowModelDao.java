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

    List<WorkflowModel> findAllByModelNameAndOpenIsTrueOrderByVersionDesc(String modelName);

    //查找可用的版本列表
    List<WorkflowModel> findAllByOpenIsTrue();

    @Query(value = "select m from WorkflowModel m where (select count(m2) from WorkflowModel m2 where m.modelName = m2.modelName and m.version < m2.version) < 1 and m.open = true order by m.version desc")
    List<WorkflowModel> getAllWorkflows();

    List<WorkflowModel> findAllByIdIn(List<Long> id);

}
