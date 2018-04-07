//package com.beeasy.hzback.modules.setting.service;
//
//import com.beeasy.hzback.modules.setting.dao.IDepartmentDao;
//import com.beeasy.hzback.modules.setting.entity.Department;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Service
//@Transactional
//public class DepartmentService2 {
//
//    @Autowired
//    private IDepartmentDao departmentDao;
//
//    public List<Department> listAsTree(){
//        return departmentDao.findByParent(null).getDepartments();
//        //得到所有部门
//
//
////        return new HashSet<Department>({top});
////        return top.getDepartments();
////
////        List<Department> all = departmentDao.findAll();
////        if(all.size() == 0){
////            return all;
////        }
////        Map<Integer,Department> map = new HashMap<Integer, Department>();
////        List<Department> result = new ArrayList<Department>();
////        for (Department department : all) {
////            map.put(department.getId(),department);
////        }
////        for(Department department : all){
////            if(department.getParentId() == 0){
////                result.add(department);
////            }
////            else{
////                if(map.containsKey(department.getParentId())){
////                    Department parent = map.get(department.getParentId());
////                    List<Department> children = parent.getChildrenDeparment();
////                    if(children == null){
////                        children = new ArrayList<Department>();
////                        parent.setChildrenDeparment(children);
////                    }
////                    children.add(department);
////                }
////            }
////        }
//    }
//
//
//
//
//    public boolean add(Department department,long parentId){
//        Department parent = departmentDao.findOne(parentId);
//        if(parent == null) return false;
//        Department d = new Department();
//        d.setName(department.getName());
//        d.setParent(parent);
//        departmentDao.save(d);
//        return d.getId() > 0;
//    }
//
//    public boolean edit(Department department){
//        Department target = departmentDao.findOne(department.getId());
//        if(target == null){
//            return false;
//        }
//        if(!StringUtils.isEmpty(department.getName())){
//            target.setName(department.getName());
//        }
//        departmentDao.save(target);
//        return true;
//    }
//
//
//
//}
