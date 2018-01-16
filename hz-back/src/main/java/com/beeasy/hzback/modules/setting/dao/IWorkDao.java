package com.beeasy.hzback.modules.setting.dao;

import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.setting.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IWorkDao extends JpaRepository<Work,Integer> {
}
