package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.entity.GlobalPermission;
import com.beeasy.hzback.modules.system.entity.WorkflowInstance;
import com.beeasy.hzback.modules.system.entity.WorkflowNodeInstance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface IWorkflowInstanceDao extends JpaRepository<WorkflowInstance,Long>{
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
    @Query("select distinct i from WorkflowInstance i, User u " +
            "join i.nodeList nl " +
//            "join nl.nodeModel nm " +
//            "join nm.persons ps " +
//            "join u.quarters q " +
            "where " +
            //节点处理人是我自己
            "( (nl.dealerId is not null and nl.dealerId in :uids) or " +
            //为空的情况,寻找可以处理的人
            "(nl.dealerId is null and u.id in ("+ IGlobalPermissionDao.SQL.GET_UIDS_WITHOUT_OID +")) ) and " +
            //该节点任务未完成
            "nl.finished = false and " +
            //任务进行中
            "i.state = 'DEALING' and " +
            //分页
            "i.id <= :lessId " +
            "order by i.addTime, i.id desc")
    Page<WorkflowInstance> findNeedToDealWorks(
            @Param("types") Collection<GlobalPermission.Type> types,
            @Param("uids") Collection<Long> uids,
            @Param("lessId") Long lessId,
            Pageable pageable
    );

    //我已执行过的任务
    @Query(value = "select distinct ins from WorkflowInstance ins join ins.nodeList nl join nl.attributeList al where al.dealUser.id in :uids and nl.finished = true and ins.id <= :lessId order by ins.addTime desc")
    Page<WorkflowInstance> findDealedWorks(@Param("uids") Collection<Long> uids, @Param("lessId") Long lessId, Pageable pageable);

    //部门未执行任务
    @Query("select distinct i from WorkflowInstance i, User u " +
//            "join i.nodeList nl " +
            "join i.workflowModel model " +
            "join model.departments d " +
//            "join nl.nodeModel nm " +
//            "join nm.persons ps " +
            "join u.quarters q " +
            "where " +
            //用户的部门是这个模型归属部门的父部门
            "( select count(dd) from Department dd where dd.id = d.id and dd.code like concat(q.department.code,'%')) > 0 and " +
            //在用户之中
            "u.id in :uids and " +
            //是主管
            "q.manager = true and " +
            //任务进行中
            "i.state = :state and " +
            //分页
            "i.id <= :lessId " +
            "order by i.addTime, i.id desc")
    Page<WorkflowInstance> findNeedToDealWorksFromDepartments(
            @Param("uids") Collection<Long> uids,
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
                    "join ins.workflowModel model " +
                    "join model.departments d " +
//                    "join ins.nodeList nl " +
//                    "join ins.workflowModel model " +
//                    "left join model.permissions ps " +
//                    "join nl.nodeModel nModel " +
//                    "join nModel.persons p " +
            "where " +
                    //三者条件满足一即可
                    "(" +
                        //用户是部门主管
                        "( select count(dd) from Department dd where dd.id = d.id and dd.code like concat(q.department.code,'%') and q.manager = true) > 0 or " +
                        //或者拥有观察岗权限
                        "user.id in (" + IGlobalPermissionDao.SQL.GET_UIDS_WITHOUT_OID + ") " +
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
            "select distinct ins from WorkflowInstance ins, User user " +
//            "join user.quarters q " +
            "join ins.workflowModel model " +
//            "join model.nodeModels nm " +
//            "join nm.persons ps " +
            "where " +
                    //是公共任务
//                    "ins.common = true and " +
                    "ins.state = 'COMMON' and " +
                    //拥有执行的权限
                    "user.id in ("+IGlobalPermissionDao.SQL.GET_UIDS_WITHOUT_OID+") and " +
                    //分页
                    "ins.id <= :lessId and " +
                    "user.id in :uids " +
                    "order by ins.addTime, ins.id desc ")
    Page<WorkflowInstance> findCommonWorks(
            @Param("types") Collection<GlobalPermission.Type> types,
            @Param("uids") Collection<Long> uids,
            @Param("lessId") Long lessId,
            Pageable pageable);

    //任务当前应该执行的节点
    @Query(value = "select nl from WorkflowInstance ins join ins.nodeList nl where ins.state = 'DEALING' and nl.finished = false and ins.id = :instanceId")
    Optional<WorkflowNodeInstance> getCurrentNodeInstance(@Param("instanceId") Long instanceId);


    //用户可以接受的任务
    @Query(value = "select ins from WorkflowInstance ins, User u " +
            "join ins.transactions t " +
            "where u.id in :uids and t.finished = false and " +
                "u.id = t.userId and " +
                "ins.id <= :lessId " +
            "order by ins.addTime, ins.id desc")
    Page<WorkflowInstance> findUserCanAcceptWorks(@Param("uids") Collection<Long> uids, @Param("lessId") long lessId, Pageable pageable);


}
