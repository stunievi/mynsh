package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.WorkflowNodeAttribute;
import com.beeasy.hzback.modules.system.entity.WorkflowNodeInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IWorkflowNodeAttributeDao extends JpaRepository<WorkflowNodeAttribute,Long> {
    Optional<WorkflowNodeAttribute> findFirstByNodeInstanceIdAndAttrKey(long niid, String attrKey);

    Optional<WorkflowNodeAttribute> findFirstByDealUserIdAndAttrKey(long uid, String key);


    @Query(value = "select attr.attrValue from WorkflowNodeAttribute attr where attr.nodeInstance.instanceId = :instanceId and attr.attrKey = :kk order by attr.nodeInstanceId desc")
    List getValueByWorkflowInstance(@Param("instanceId") long instanceId, @Param("kk") String kk);
}
