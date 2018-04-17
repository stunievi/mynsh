package com.beeasy.hzback.modules.system.service;


import bin.leblanc.classtranslate.Transformer;
import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.modules.system.dao.IDepartmentDao;
import com.beeasy.hzback.modules.system.entity.Department;
import com.beeasy.hzback.modules.system.form.DepartmentAdd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DepartmentService implements IDepartmentService{

    @Autowired
    IDepartmentDao departmentDao;

    @Override
    public Department createDepartment(DepartmentAdd add) throws RestException {
        Department same = departmentDao.findByName(add.getName());
        if(same != null) throw new RestException("已有同名部门");

        Department department = Transformer.transform(add,Department.class);
        //parent

        if(add.getParentId() != null && add.getParentId() > 0){
            Department parent = departmentDao.findOne(add.getParentId());
            if(parent == null){
                return null;
            }
            department.setParent(parent);
        }
        Department result = departmentDao.save(department);
        return result;
    }


    @Override
    public void deleteDepartment(long id){
        departmentDao.delete(id);
    }


}
