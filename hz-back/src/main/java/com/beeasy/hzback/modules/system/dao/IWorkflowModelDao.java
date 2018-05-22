package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.User;
import com.beeasy.hzback.modules.system.entity.WorkflowModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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


    //得到一个模型起始节点的可处理人
    @Query(value = "select user from WorkflowModel m, User user join user.quarters qs join m.nodeModels nm join nm.persons ps where ps.type = 0 and nm.start = true and m.id = :id and ps.uid = qs.id")
    List<User> getFirstNodeUsers(@Param("id") Long id);

    //得到当前节点的可处理人
    @Query(value = "select user from WorkflowModel m, User user join user.quarters qs join m.nodeModels nm join nm.persons ps where ps.type = 0 and nm.id = :id and ps.uid = qs.id")
    List<User> getNodeUsers(@Param("id") Long id);

}
