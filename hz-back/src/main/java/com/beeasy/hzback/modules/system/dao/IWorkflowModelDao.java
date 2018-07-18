package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.User;
import com.beeasy.hzback.modules.system.entity.WorkflowModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IWorkflowModelDao extends JpaRepository<WorkflowModel,Long>, JpaSpecificationExecutor{
    WorkflowModel findFirstByNameAndVersion(String name, BigDecimal version);
    Page<List<WorkflowModel>> findAllByName(String name, Pageable pageable);

    List<WorkflowModel> findAllByModelNameAndOpenIsTrueOrderByVersionDesc(String modelName);

    //查找可用的版本列表
    List<WorkflowModel> findAllByOpenIsTrue();


    //查找同名工作流是否有相同版本
    int countByNameAndOpenIsTrue(String name);
    Optional<WorkflowModel> findTopByModelNameAndOpenIsTrue(final String modelName);
    List<WorkflowModel> findAllByModelNameLikeAndOpenIsTrue(final String modelName);

    Optional<WorkflowModel> findFirstByModelNameAndOpenIsTrueOrderByVersionDesc(String modelName);

    @Query(value = "select m.id from WorkflowModel m where m.modelName = :modelName and m.open = true order by m.version desc ")
    List<Long> findModelId(@Param("modelName") String modelName);

    @Query(value = "select m from WorkflowModel m where m.open = true order by m.version desc")
    List<WorkflowModel> getAllWorkflows();

    List<WorkflowModel> findAllByIdIn(List<Long> id);


//    //判断一个用户是不是某个工作流的主管
//    @Query(value = "select count(u.id) from WorkflowModel model, User u, GlobalPermission gp " +
////            "join model.departments d " +
//            "join u.quarters q " +
//            "where " +
//                //声明关联
//                "gp.objectId = model.id and gp.type = 'WORKFLOW_PUB' and " +
//                //声明限制条件
//                "q.manager = true and model.id = :mid and u.id = :uid and " +
//                //
//                "(" +
//                    //该用户是否是这个岗位的主管
//                    "(gp.userType = 'QUARTER' and (select count(qq) from Quarters qq where qq.id = gp.linkId and qq.code like concat(q.department.code,'_%')) > 0 ) or " +
//                    //该用户是否是授权用户所持有的岗位的主管
//                    "(gp.userType = 'USER' and (select count(uuqq) from User uu join uu.quarters uuqq where uu.id = gp.linkId and uuqq.code like concat(q.department.code,'_%') ) > 0 )" +
//                ")")
//    int isManagerForWorkflow(@Param("uid") long uid, @Param("mid") long mid);

    @Modifying
    @Query(value = "update WorkflowModel set depIds = :depIds where id = :id")
    int updateWorkflowModelDeps(@Param("id") long id, @Param("depIds") String depIds);

    //软删除
    @Modifying
    @Query(value = "update WorkflowModel set deleted = true where id = :id")
    int deleteWorkflowModel(@Param("id") long id);

    //得到一个模型起始节点的可处理人
//    @Query(value = "select user.id from WorkflowModel m, User user join user.quarters qs join m.nodeModels where nm.start = true and m.id = :id")
//    List<Long> getFirstNodeUsers(@Param("id") Long id);

    //得到当前节点的可处理人
//    @Query(value = "select user from WorkflowModel m, User user join user.quarters qs join m.nodeModels nm join nm.persons ps where ps.type = 0 and nm.id = :id and ps.uid = qs.id")
//    List<User> getNodeUsers(@Param("id") Long id);

}
