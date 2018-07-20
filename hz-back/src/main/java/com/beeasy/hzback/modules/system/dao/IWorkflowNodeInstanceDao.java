package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.WorkflowInstance;
import com.beeasy.hzback.modules.system.entity.WorkflowModel;
import com.beeasy.hzback.modules.system.entity.WorkflowNode;
import com.beeasy.hzback.modules.system.entity.WorkflowNodeInstance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IWorkflowNodeInstanceDao extends JpaRepository<WorkflowNodeInstance,Long> {

    @Query(
            value = "select node.instance from WorkflowNodeInstance node join node.instance instance where instance.workflowModel in :models and node.nodeName in :names and node.finished = :finished group by node.instance",
            countQuery = "select count(node) from WorkflowNodeInstance node join node.instance instance where instance.workflowModel in :models and node.nodeName in :names and node.finished = :finished group by node.instance")
    Page<WorkflowInstance> getInstanceList(@Param("models") Set<WorkflowModel> models, @Param("names") Set<String> names, @Param("finished") boolean finished, Pageable pageable);


    @Query("select n from WorkflowNodeInstance n where n.nodeModel.type = :type and n.finished = false")
    Page<WorkflowNodeInstance> getCurrentNode(@Param("type") WorkflowNode.Type type, Pageable pageable);


    Optional<WorkflowNodeInstance> findFirstByInstanceAndFinishedIsFalse(WorkflowInstance instance);
    Optional<WorkflowNodeInstance> findFirstByInstance_IdAndFinishedIsFalse(Long instanceId);
    Optional<WorkflowNodeInstance> findFirstByIdAndFinishedIsFalse(long id);

    //得到当前正在执行的节点
    @Query(value = "select n from WorkflowNodeInstance n where n.instanceId = :instanceId and n.instance.state = 'DEALING' and n.finished = false ")
    Optional<WorkflowNodeInstance> getCurrentNodeInstance(@Param("instanceId") long instanceId);


    @Query(value = "select id from WorkflowNodeInstance where nodeModel.start = true and instanceId = :instanceId")
    List getStartNodeIds(@Param("instanceId") long instanceId);

//    @Query(value = "select distinct child from WorkflowNodeInstance ni join ni.childInstances child where ni.instanceId = :id and child.dealUserId = :uid")
//    List<WorkflowInstance> getAllChildInstance(@Param("id") long id, @Param("uid") long uid);

    @Modifying
    @Query(value = "update WorkflowNodeInstance ni set ni.dealerId = :uid where ni.id in :ids ")
    int updateNodeInstanceDealer(@Param("uid") long uid, @Param("ids") Collection<Long> ids);

    Optional<WorkflowNodeInstance> findTopByNodeModel_NameAndInstanceIdOrderByIdDesc(String name, long instanceId);

    //查询其实节点实例
    List<WorkflowNodeInstance> findAllByInstanceIdAndNodeModel_StartIsTrue(long instanceId);

    //是否已经有过这个节点
    int countByInstanceIdAndNodeModelId(long iid, long nmid);
    Optional<WorkflowNodeInstance> findTopByInstanceIdAndNodeModelIdAndIdLessThanOrderByIdDesc(long iid, long nmid, long id);
}
