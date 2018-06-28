package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.InfoCollectLink;
import com.beeasy.hzback.modules.system.entity.WorkflowInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface IInfoCollectLinkDao extends JpaRepository<InfoCollectLink,Long>{
    @Query(value = "select link.instance from InfoCollectLink link where link.billNo = :billNo")
    List<WorkflowInstance> getInstancesByBillNo(@Param("billNo") String billNo);

    int deleteAllByBillNoAndInstanceIdIn(String billNo, Collection<Long> ids);
}
