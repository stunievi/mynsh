package com.beeasy.hzback.modules.setting.service;

import com.beeasy.hzback.modules.setting.dao.IDepartmentDao;
import com.beeasy.hzback.modules.setting.entity.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DepartmentService {

    @Autowired
    private IDepartmentDao departmentDao;

    public List<Department> listAsTree(){
        //得到所有部门
        List<Department> all = departmentDao.findAll();
        if(all.size() == 0){
            return all;
        }
        Map<Integer,Department> map = new HashMap<Integer, Department>();
        List<Department> result = new ArrayList<Department>();
        for (Department department : all) {
            map.put(department.getId(),department);
        }
        for(Department department : all){
            if(department.getParentId() == 0){
                result.add(department);
            }
            else{
                if(map.containsKey(department.getParentId())){
                    Department parent = map.get(department.getParentId());
                    List<Department> children = parent.getChildrenDeparment();
                    if(children == null){
                        children = new ArrayList<Department>();
                        parent.setChildrenDeparment(children);
                    }
                    children.add(department);
                }
            }
        }
        return result;
    }

//    public List<Department> getChildrenDepartment(Department department){
//
//    }
}
