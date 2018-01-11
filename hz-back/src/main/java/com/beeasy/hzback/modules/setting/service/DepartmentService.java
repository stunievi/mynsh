package com.beeasy.hzback.modules.setting.service;

import com.beeasy.hzback.modules.setting.dao.IDepartmentDao;
import com.beeasy.hzback.modules.setting.entity.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class DepartmentService {

    @Autowired
    private IDepartmentDao departmentDao;

    public Set<Department> listAsTree(){
        //得到所有部门
        Department top = departmentDao.findOne(1);
        makeChildren(top);
        return top.getDepartments();
//        return new HashSet<Department>({top});
//        return top.getDepartments();
//
//        List<Department> all = departmentDao.findAll();
//        if(all.size() == 0){
//            return all;
//        }
//        Map<Integer,Department> map = new HashMap<Integer, Department>();
//        List<Department> result = new ArrayList<Department>();
//        for (Department department : all) {
//            map.put(department.getId(),department);
//        }
//        for(Department department : all){
//            if(department.getParentId() == 0){
//                result.add(department);
//            }
//            else{
//                if(map.containsKey(department.getParentId())){
//                    Department parent = map.get(department.getParentId());
//                    List<Department> children = parent.getChildrenDeparment();
//                    if(children == null){
//                        children = new ArrayList<Department>();
//                        parent.setChildrenDeparment(children);
//                    }
//                    children.add(department);
//                }
//            }
//        }
    }

    private void makeChildren(Department department){
        Set<Department> children = department.getDepartments();
        for (Department child : children) {
            makeChildren(child);
        }
        department.setDepartments(children);
    }



}
