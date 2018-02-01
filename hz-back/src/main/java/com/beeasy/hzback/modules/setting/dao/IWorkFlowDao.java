package com.beeasy.hzback.modules.setting.dao;

import com.beeasy.hzback.modules.setting.entity.WorkFlow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IWorkFlowDao extends JpaRepository<WorkFlow,Integer> {
}
