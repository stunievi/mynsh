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
public class QuartersService implements IQuartersService{

    @Autowired
    IDepartmentDao departmentDao;

    @Autowired
    IQuartersDao quartersDao;

    @Override
    public Quarters createQuarters(QuartersAdd add) throws RestException {
        Department department = departmentDao.findOne(add.getDepartmentId());
        if(department == null) return null;
        //同部门不能有同名的岗位
        Quarters same = quartersDao.findFirstByDepartmentAndName(department,add.getName());
        if(same != null) throw new RestException("已经有同名的岗位");

        Quarters quarters = Transformer.transform(add,Quarters.class);
        quarters.setDepartment(department);
        Quarters ret = quartersDao.save(quarters);
        return ret;
    }

    @Override
    public void deleteQuarters(long quartersId) {
        quartersDao.delete(quartersId);
    }
}
