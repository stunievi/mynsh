package com.beeasy.hzback.modules.system.service;


import com.beeasy.hzback.modules.system.dao.IDepartmentDao;
import com.beeasy.common.entity.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional
public class DepartmentService {

    @Autowired
    IDepartmentDao departmentDao;


//    @Override

    public List<Department> findDepartments(String name, Long parentId) {
        //name比parent优先
        if (!StringUtils.isEmpty(name)) {
            return (departmentDao.findAllByName(name));
        }
        if (parentId == null || parentId.equals(0L)) {
            return departmentDao.findAllByParent(null);
        } else {
            return departmentDao.findAllByParent_Id(parentId);
        }
    }


}
