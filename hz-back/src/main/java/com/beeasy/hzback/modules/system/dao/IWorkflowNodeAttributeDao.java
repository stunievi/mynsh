package com.beeasy.hzback.modules.system.dao;

import com.beeasy.common.entity.WorkflowNodeAttribute;
import com.beeasy.common.entity.WorkflowNodeInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IWorkflowNodeAttributeDao extends JpaRepository<WorkflowNodeAttribute, Long> {
    Optional<WorkflowNodeAttribute> findFirstByNodeInstanceIdAndAttrKey(long niid, String attrKey);

    Optional<WorkflowNodeAttribute> findTopByNodeInstanceIdAndDealUserIdAndAttrKey(long niid, long uid, String attrKey);

    Optional<WorkflowNodeAttribute> findFirstByDealUserIdAndAttrKey(long uid, String key);


    @Query(value = "select attr.attrValue from WorkflowNodeAttribute attr where attr.nodeInstance.instanceId = :instanceId and attr.attrKey = :kk order by attr.nodeInstanceId desc")
    List getValueByWorkflowInstance(@Param("instanceId") long instanceId, @Param("kk") String kk);

    //得到所有的处理人
    @Query(value = "select distinct at.dealUserId from WorkflowNodeAttribute  at where at.dealUserId is not null and at.nodeInstanceId = :nid")
    List<Long> getUidsByNodeInstnce(@Param("nid") long nid);

    int countByNodeInstanceIdAndAttrKeyAndAttrValue(final long iid, final String key, final String value);

    int countByNodeInstanceIdAndDealUserIdAndAttrKey(final long niid, final long uid, final String key);
}
