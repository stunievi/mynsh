package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.WorkflowInstance;
import com.beeasy.hzback.modules.system.entity.WorkflowNodeInstance;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IWorkflowInstanceDao extends JpaRepository<WorkflowInstance,Long>{
    List<WorkflowInstance> findAllByIdIn(List<Long> ids);

    //我执行的任务
    List<WorkflowInstance> findAllByDealUser_IdAndIdLessThanOrderByAddTimeDesc(long uid, long lessId, Pageable pageable);
    List<WorkflowInstance> findAllByDealUser_IdOrderByAddTimeDesc(long uid, Pageable pageable);

    //我发布的任务
    List<WorkflowInstance> findAllByPubUser_IdAndIdLessThanOrderByAddTimeDesc(long uid, long lessId, Pageable pageable);
//    List<WorkflowInstance> findAllByPubUser_IdOrderByAddTimeDesc(long uid, Pageable pageable);


    //我观察的任务

    //我未执行的任务
    @Query("select distinct i from WorkflowInstance i, User u " +
            "join i.nodeList nl " +
            "join nl.nodeModel nm " +
            "join nm.persons ps " +
            "join u.quarters q " +
            "where " +
            //节点处理人是我自己
            "( (nl.dealer is not null and nl.dealer.id in :uids) or " +
            //为空的情况,寻找可以处理的人
            "(nl.dealer is null and ps.type = 1 and ps.uid = q.id) ) and " +
            //该节点任务未完成
            "nl.finished = false and " +
            //任务进行中
            "i.state = 1 and " +
            //分页
            "i.id < :lessId " +
            "order by i.addTime desc")
    List<WorkflowInstance> findNeedToDealWorks(@Param("uids") List<Long> uids, @Param("lessId") Long lessId, Pageable pageable);

    //我已执行过的任务
    @Query(value = "select distinct ins from WorkflowInstance ins join ins.nodeList nl join nl.attributeList al where al.dealUser.id in :uids and nl.finished = true and ins.id < :lessId order by ins.addTime desc")
    List<WorkflowInstance> findDealedWorks(@Param("uids") List<Long> uids, @Param("lessId") Long lessId, Pageable pageable);


    //我观察的任务
    //1.任务模型原本配置的观察者岗位
    //2.存于任务表中的观察者
    @Query(value = "select distinct ins from WorkflowInstance ins, User user join user.quarters q join ins.nodeList nl join ins.workflowModel model left join model.permissions ps join nl.nodeModel nModel join nModel.persons p where ( (ps.type = 1 and ps.qid = q.id) or (nl.finished = true and p.uid = q.id)) and ins.id < :lessId and user.id in :uids")
    List<WorkflowInstance> findObserveredWorks(@Param("uids") List<Long> uids, @Param("lessId") Long lessId, Pageable pageable);


    //任务当前应该执行的节点
    @Query(value = "select nl from WorkflowInstance ins join ins.nodeList nl where ins.state = 1 and nl.finished = false and ins.id = :instanceId")
    Optional<WorkflowNodeInstance> getCurrentNodeInstance(@Param("instanceId") Long instanceId);

}
