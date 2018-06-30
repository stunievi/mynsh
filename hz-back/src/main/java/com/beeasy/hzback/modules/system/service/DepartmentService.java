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
public class DepartmentService implements IDepartmentService {

    @Autowired
    IDepartmentDao departmentDao;

    @Override
    public Result<Department> createDepartment(DepartmentAdd add) {
        Optional<Department> same;
        Department parent = null;
        if (null == add.getParentId()) {
            same = departmentDao.findFirstByParentAndName(null, add.getName());
        } else {
            same = departmentDao.findFirstByParentAndName(parent = findDepartment(add.getParentId()).orElse(null), add.getName());
        }
        if (same.isPresent()) return Result.error("已有同名部门");

        Department department = new Department();
        department.setName(add.getName());
        department.setParentId(null == parent ? null : parent.getId());
        department.setInfo(add.getInfo());
        department.setSort(add.getSort());

        //部门编号
        List objs;
        if(null == department.getParentId()){
             objs = departmentDao.getTopLastCode();
        }
        else{
            objs = departmentDao.getLastCode(department.getParentId());
        }
        System.out.println(objs);
        //没找到的时候,使用父编码+001
        if(objs.size() == 0){
            if(null == department.getParentId()){
                department.setCode("001");
            }
            else{
                List codes = departmentDao.getDepartmentCode(department.getParentId());
                department.setCode(codes.get(0) + "001");
            }
        }
        else{
            //取后三位+1
            String code = (String) objs.get(0);
            int codeValue = Integer.valueOf(code.substring(code.length() - 3, code.length()));
            codeValue++;
            //补足3位
            String newCode = String.valueOf(codeValue);
            for(int i = newCode.length(); i < 3; i++){
                newCode = "0" + newCode;
            }
            department.setCode(code.substring(0,code.length() - 3) + newCode);
        }
        department = departmentDao.save(department);

        return Result.finish(department.getId() != null, department);
    }


    public Result<Department> editDepartment(DepartmentEdit edit) {
        Result result = Result.error();
        findDepartment(edit.getId()).ifPresent(department -> {
//            if(null == department.getParent()){
//                result.setErrMessage("顶级部门禁止编辑");
//                return;
//            }
            if (!StringUtils.isEmpty(edit.getName()) && !department.getName().equals(edit.getName())) {
                //校验是否同名
                Optional<Department> same = departmentDao.findFirstByParentAndName(department.getParent(), edit.getName());
                if (same.isPresent()) {
                    result.setErrMessage("已经存在同名部门");
                    return;
                }
                department.setName(edit.getName());
            }

            if (!StringUtils.isEmpty(edit.getInfo())) {
                department.setInfo(edit.getInfo());
            }

            if (edit.getParentId() != null && !department.getParent().getId().equals(edit.getParentId())) {
                Optional<Department> newParent = findDepartment(edit.getParentId());
                if (!newParent.isPresent()) {
                    result.setErrMessage("没找到要设置的父部门");
                    return;
                }
                if (department.getChildren().size() > 0) {
                    result.setErrMessage("还有子部门, 无法移动");
                    return;
                }
                if (department.getQuarters().size() > 0) {
                    result.setErrMessage("还有岗位存在, 无法移动");
                    return;
                }
                department.setParentId(newParent.get().getId());
            }
            //排序
            department.setSort(edit.getSort());
            result.setSuccess(true);
            result.setData(departmentDao.save(department));
        });
        return result;
    }


    @Override
    public Result deleteDepartment(long id) {
        return findDepartment(id).map(department -> {
//            if(department.getParent() == null){
//                return Result.error("顶级部门禁止删除");
//            }
            //如果还要岗位 不能删除
            if (department.getQuarters().size() > 0) {
                return Result.error("该部门还有岗位, 无法删除");
            }

            if (department.getChildren().size() > 0) {
                return Result.error("该部门还有子部门, 无法删除");
            }

            departmentDao.delete(id);

            return Result.ok();

        }).orElse(Result.error());
    }

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


    public Optional<Department> findDepartment(long id) {
        return Optional.ofNullable(departmentDao.findOne(id));
    }
}
