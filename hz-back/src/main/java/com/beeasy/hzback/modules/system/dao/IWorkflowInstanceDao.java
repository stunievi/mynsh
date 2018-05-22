package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.WorkflowInstance;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

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
    @Query("select i from WorkflowInstance i " +
            "join i.nodeList nl " +
            "join nl.nodeModel nm " +
            "join nm.persons ps " +
            "where " +
            //节点处理人是我自己
            "( (nl.dealer is not null and nl.dealer.id in :uids) or" +
            //为空的情况,寻找可以处理的人
            "(nl.dealer is null and ((ps.type = 1 and ps.uid in :uids) or (ps.type = 0 and ps.uid in :qids))) ) and " +
            //该节点任务未完成
            "nl.finished = false and " +
            //任务进行中
            "i.state = 1 and " +
            //分页
            "i.id < :lessId " +
            "group by i.id order by i.addTime desc")
    List<WorkflowInstance> findNeedToDealWorks(List<Long> uids, List<Long> qids, Long lessId, Pageable pageable);

    //我已执行过的任务


}
