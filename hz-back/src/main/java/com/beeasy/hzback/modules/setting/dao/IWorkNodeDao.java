package com.beeasy.hzback.modules.setting.dao;

import com.beeasy.hzback.modules.setting.entity.Work;
import com.beeasy.hzback.modules.setting.entity.WorkNode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IWorkNodeDao extends JpaRepository<WorkNode,Integer> {
    void deleteAllByWork(Work work);
}
