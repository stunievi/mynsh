package com.beeasy.hzback.modules.setting.dao;

import com.beeasy.hzback.core.helper.SpringContextUtils;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.setting.entity.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;


public interface IWorkDao extends JpaRepository<Work,Integer> {



    @Transactional
    default void updateNodeList() throws RuntimeException {
        this.deleteAll();
        Work work = new Work();
        work.setInfo("c");
        work.setName("f");
        this.save(work);
    }


}
