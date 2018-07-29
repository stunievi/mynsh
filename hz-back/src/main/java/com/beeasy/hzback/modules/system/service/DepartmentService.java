package com.beeasy.hzback.modules.system.service;


import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.modules.system.dao.IDepartmentDao;
import com.beeasy.hzback.modules.system.entity.Department;
import com.beeasy.hzback.modules.system.form.DepartmentAdd;
import com.beeasy.hzback.modules.system.form.DepartmentEdit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DepartmentService {

    @Autowired
    IDepartmentDao departmentDao;



//    @Override

    public List<Department> findDepartments(String name, Long parentId){
        //name比parent优先
        if(!StringUtils.isEmpty(name)){
            return (departmentDao.findAllByName(name));
        }
        if(parentId != null && parentId.equals(0)){
            parentId = null;
        }
        return departmentDao.findAllByParent_Id(parentId);
    }


}
