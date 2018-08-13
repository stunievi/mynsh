package com.beeasy.hzback.modules.system.dao;

import com.beeasy.common.entity.InfoCollectLink;
import com.beeasy.common.entity.WorkflowInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IInfoCollectLinkDao extends JpaRepository<InfoCollectLink,Long>{
    @Query(value = "select link.instance from InfoCollectLink link where link.billNo = :billNo")
    List<WorkflowInstance> getInstancesByBillNo(@Param("billNo") String billNo);

    int deleteAllByBillNoAndInstanceIdIn(String billNo, Collection<Long> ids);
    int countByBillNoAndInstanceId(String billNo, long id);

    Optional<InfoCollectLink> findTopByInstanceId(final long instanceId);
}
