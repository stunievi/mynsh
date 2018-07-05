package com.beeasy.hzback.modules.system.service;

import bin.leblanc.classtranslate.Transformer;
import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.modules.system.dao.IDepartmentDao;
import com.beeasy.hzback.modules.system.dao.IQuartersDao;
import com.beeasy.hzback.modules.system.entity.Department;
import com.beeasy.hzback.modules.system.entity.Quarters;
import com.beeasy.hzback.modules.system.form.QuartersAdd;
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
