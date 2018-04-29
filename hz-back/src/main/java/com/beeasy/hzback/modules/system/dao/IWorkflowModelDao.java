package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.WorkflowModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface IWorkflowModelDao extends JpaRepository<WorkflowModel,Long>{
    WorkflowModel findFirstByNameAndVersion(String name, BigDecimal version);
    Page<List<WorkflowModel>> findAllByName(String name, Pageable pageable);

    List<WorkflowModel> findAllByModelNameAndOpenIsTrueOrderByVersionDesc(String modelName);

    //查找可用的版本列表
    List<WorkflowModel> findAllByOpenIsTrue();
}
