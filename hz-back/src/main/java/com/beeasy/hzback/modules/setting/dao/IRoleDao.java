package com.beeasy.hzback.modules.setting.dao;

import com.beeasy.hzback.core.helper.SpringContextUtils;
import com.beeasy.hzback.modules.setting.entity.Department;
import com.beeasy.hzback.modules.setting.entity.Role;
import com.beeasy.hzback.modules.setting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface IRoleDao extends JpaRepository<Role,Integer> {

    default Role editByDepartmentId(Role role,Integer departmentId){
        IDepartmentDao departmentDao = (IDepartmentDao) SpringContextUtils.getBean(IDepartmentDao.class);
        if(departmentId == null){
            return null;
        }
        //验证是否存在着个部门
        Department department = departmentDao.findOne(departmentId);
        if(department == null){
            return null;
        }
        role.setDepartment(department);
        if(role.getId() == 0){
            role.setId(null);
        }
        Role result = this.save(role);
        return result;
    }

    Set<Role> findAllByUsers(Set<User> users);
}
