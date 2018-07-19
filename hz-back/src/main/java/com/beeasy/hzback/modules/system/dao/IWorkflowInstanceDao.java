package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.entity.GlobalPermission;
import com.beeasy.hzback.modules.system.entity.WorkflowInstance;
import com.beeasy.hzback.modules.system.entity.WorkflowNodeInstance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface IWorkflowInstanceDao extends JpaRepository<WorkflowInstance,Long>, JpaSpecificationExecutor {



    List<WorkflowInstance> findAllByIdIn(List<Long> ids);

    // 根据模型名选取所有实例
//    @Query(value = "SELECT w.* FROM t_workflow_instance w JOIN t_workflow_model m on m.id=w.workflow_model_id WHERE m.model_name='资料收集';", nativeQuery = true)
    @Query(value = "select ins from WorkflowInstance ins where ins.workflowModel.modelName = :modelName and ins.dealUserId = :uid")
    Page<WorkflowInstance> getInsByModelName(@Param("modelName") String modelName, @Param("uid") Long uid, Pageable pageable);

    /*
    * @gotomars
    * */
    @Query(value = "select ins from WorkflowInstance ins where ins.dealUserId = :uid")
    Page<WorkflowInstance> getAllIns( @Param("uid") Long uid, Pageable pageable);


    // 手机，我执行的任务
    List<WorkflowInstance> findAllByDealUserIdAndIdLessThanOrderByAddTimeDesc(long uid, long lessId, Pageable pageable);
    // 我执行的任务
    List<WorkflowInstance> findAllByDealUserIdOrderByAddTimeDesc(long uid, Pageable pageable);
    // 我执行的任务
    Page<WorkflowInstance> findAllByDealUserIdAndIdIsNotNullOrderByAddTimeDesc(long uid, Pageable pageable);

    // 手机，我发布的任务
    List<WorkflowInstance> findAllByPubUserIdAndIdLessThanOrderByAddTimeDesc(long uid, long lessId, Pageable pageable);
    // 我发布的任务
    Page<WorkflowInstance> findAllByPubUserIdOrderByAddTimeDesc(long uid, Pageable pageable);

    //我观察的任务

    //我未执行的任务
    @Query("select distinct i from " +
                "WorkflowInstance i " +
                "left join i.nodeList nl " +
                "left join nl.dealers dls " +
//                "left join u.quarters uq " +
                "where " +
                "(" +
                "   (dls.type = 'CAN_DEAL' and dls.userId = :uid) or " +
                "   (dls.type = 'DID_DEAL' and dls.userId = :uid)" +
                ") and " +
                //节点处理人是我自己
//                "( (nl.dealerId is not null and nl.dealerId in :uids) or " +
//                为空的情况,寻找可以处理的人
//                "(nl.dealerId is null and nl.nodeModelId in :oids ) ) and " +
                //该节点任务未完成
                "nl.finished = false and " +
                //任务进行中
                "i.state = 'DEALING' and " +
                //分页
                "i.id <= :lessId")
    Page<WorkflowInstance> findNeedToDealWorks(
            @Param("uid") long uid,
            @Param("lessId") Long lessId,
            Pageable pageable
    );

    //我已执行过的任务
    @Query(value = "select distinct ins from WorkflowInstance ins " +
            "left join ins.nodeList nl " +
            "left join nl.dealers dl " +
            "where dl.userId in :uids and dl.type = 'DID_DEAL' and nl.finished = true and ins.id <= :lessId order by ins.addTime desc")
    Page<WorkflowInstance> findDealedWorks(@Param("uids") Collection<Long> uids, @Param("lessId") Long lessId, Pageable pageable);

    //部门未执行任务
    @Query("select distinct i from WorkflowInstance i " +
//            "join i.nodeList nl " +
//            "join i.workflowModel model " +
//            "join model.departments d " +
//            "join nl.nodeModel nm " +
//            "join nm.persons ps " +
            "where " +
            //用户的部门是这个模型归属部门的父部门
            "i.depId in :dids and " +
            //在用户之中
            //任务进行中
            "i.state = :state and " +
            //分页
            "i.id <= :lessId " +
            "order by i.addTime, i.id desc")
    Page<WorkflowInstance> findNeedToDealWorksFromDepartments(
            @Param("dids") Collection<Long> dids,
            @Param("state") WorkflowInstance.State state,
            @Param("lessId") Long lessId,
            Pageable pageable
    );


    //我观察的任务
    //1.任务模型原本配置的观察者岗位
    //2.存于任务表中的观察者
    @Query(value =
            "select distinct ins from WorkflowInstance ins, User user " +
                    "join user.quarters q " +
//                    "join ins.workflowModel model " +
//                    "join model.departments d " +
//                    "join ins.nodeList nl " +
//                    "join ins.workflowModel model " +
//                    "left join model.permissions ps " +
//                    "join nl.nodeModel nModel " +
//                    "join nModel.persons p " +
            "where " +
                    //三者条件满足一即可
                    "(" +
                        //用户是部门主管
//                        "( select count(dd) from Department dd where dd.id = d.id and dd.code like concat(q.department.code,'%') and q.manager = true) > 0 or " +
                        //或者拥有观察岗权限
                        "ins.modelId in (" + IGlobalPermissionDao.SQL.GET_OIDS_WITH_UIDS + ") " +
                        //或者是曾经执行过的任务
                        //暂时不这么搞
//                        "(select count(ob) from WorkflowInstanceObserver ob where ob.userId = user.id and ob.instanceId = ins.id) > 0" +
                    //限制用户
                    ") and user.id in :uids and " +
                    //
                    //分页
                    "ins.id <= :lessId and user.id in :uids order by ins.addTime, ins.id desc")
    Page<WorkflowInstance> findObserveredWorks(
            @Param("types") Collection<GlobalPermission.Type> types,
            @Param("uids") Collection<Long> uids,
            @Param("lessId") Long lessId,
            Pageable pageable);

//    //我可以执行的公共任务
    @Query(value =
            "select distinct ins from WorkflowInstance ins, GlobalPermission gp, User user " +
            "left join user.quarters q " +
//            "join ins.workflowModel model " +
//            "join model.nodeModels nm " +
//            "join nm.persons ps " +
            "where " +
                    //是公共任务
//                    "ins.common = true and " +
                    "ins.state = 'COMMON' and " +
                    //拥有执行的权限
                    "(select count(d) from Department d where q.code like concat(d.code,'_%') and d.id = ins.depId) > 0 and " +
                    //
                    "(gp.type = 'WORKFLOW_PUB' and gp.linkId = q.id and gp.objectId = ins.modelId) and " +
//                    "model.id in ("+IGlobalPermissionDao.SQL.GET_OIDS_WITH_UIDS+") and " +
                    //分页
                    "ins.id <= :lessId and " +
                    "user.id = :uid "
                    )
    Page<WorkflowInstance> findCommonWorks(
//            @Param("types") Collection<GlobalPermission.Type> types,
            @Param("uid") long uid,
            @Param("lessId") Long lessId,
            Pageable pageable);

    //任务当前应该执行的节点
    @Query(value = "select nl from WorkflowInstance ins join ins.nodeList nl where ins.state = 'DEALING' and nl.finished = false and ins.id = :instanceId")
    Optional<WorkflowNodeInstance> getCurrentNodeInstance(@Param("instanceId") Long instanceId);


    //用户可以接受的任务
    @Query(value = "select distinct ins from WorkflowInstance ins, User u " +
            "join ins.transactions t " +
            "where u.id in :uids and t.state = 'DEALING' and " +
                "u.id = t.userId and " +
                "ins.id <= :lessId " +
            "order by ins.addTime, ins.id desc")
    Page<WorkflowInstance> findUserCanAcceptWorks(@Param("uids") Collection<Long> uids, @Param("lessId") long lessId, Pageable pageable);


    //得到预任务
    Page<WorkflowInstance> findAllByDealUserIdInAndPlanStartTimeGreaterThanAndIdLessThan(Collection<Long> uids, Date time, long lessId, Pageable pageable);

    //查模型名的任务
    Page<WorkflowInstance> findAllByWorkflowModel_ModelNameAndDealUserIdOrderByAddTimeDesc(String name, long uid, Pageable pageable);



    /*******台账相关******/
    @Query(value = "select distinct ins from WorkflowInstance ins left join ins.attributes at where at.attrKey = 'BILL_NO' and at.attrValue = :billNo and ins.workflowModel.modelName = :modelName order by ins.id desc")
    Page<WorkflowInstance> getBindedWorks(@Param("billNo") String billNo, @Param("modelName") String modelName, Pageable pageable);

    /**********/
//    @Query(value = "SELECT * from ACC_LOAN ORDER BY BILL_NO DESC /*#pageable*/",
//            countQuery = "SELECT COUNT(*) FROM ACC_LOAN",
//            nativeQuery = true)
//    Page<Object> getAccLoanList(Pageable pageable);

}
