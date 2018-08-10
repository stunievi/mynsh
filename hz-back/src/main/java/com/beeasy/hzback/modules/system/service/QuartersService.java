package com.beeasy.hzback.modules.system.service;

import com.beeasy.hzback.modules.system.dao.IDepartmentDao;
import com.beeasy.hzback.modules.system.dao.IQuartersDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class QuartersService {

    @Autowired
    IDepartmentDao departmentDao;

    @Autowired
    IQuartersDao quartersDao;

    public void deleteQuarters(long quartersId) {
        quartersDao.deleteById(quartersId);
    }
}
