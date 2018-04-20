package com.beeasy.hzback;

import bin.leblanc.faker.Faker;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.modules.system.dao.IDepartmentDao;
import com.beeasy.hzback.modules.system.entity.Department;
import com.beeasy.hzback.modules.system.form.DepartmentAdd;
import com.beeasy.hzback.modules.system.form.DepartmentEdit;
import com.beeasy.hzback.modules.system.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestDepartment {

    @Autowired
    IDepartmentDao departmentDao;
    @Autowired
    DepartmentService departmentService;


    private Department createDepartment(Department parent){
        DepartmentAdd departmentAdd = new DepartmentAdd();
        departmentAdd.setName(Faker.getName());
        departmentAdd.setInfo(Faker.getName());
        departmentAdd.setParentId(parent == null ? null : parent.getId());
        Result<Department> result = departmentService.createDepartment(departmentAdd);

        assertTrue(result.isSuccess());
        return result.getData();
    }

    @Test
    public void test1(){
        //add
        Department parent = createDepartment(null);
        Department child = createDepartment(parent);
        Department grandChild = createDepartment(child);

        //edit success
        DepartmentEdit edit = new DepartmentEdit();
        edit.setName(Faker.getName());
        edit.setId(child.getId());
        Result result = departmentService.editDepartment(edit);
        assertTrue(result.isSuccess());

        //edit failed
        edit = new DepartmentEdit();
        edit.setId(parent.getId());
        edit.setName(Faker.getName());
        result = departmentService.editDepartment(edit);
        assertFalse(result.isSuccess());

        //can not edit parent
        Department newChild = createDepartment(parent);
        edit = new DepartmentEdit();
        edit.setId(child.getId());
        edit.setParentId(newChild.getId());
        result = departmentService.editDepartment(edit);
        assertFalse(result.isSuccess());

        //cannot delete
        result = departmentService.deleteDepartment(parent.getId());
        assertFalse(result.isSuccess());

        //find
        List<Department> departments = departmentService.findDepartments(null,null);
        assertTrue(departments.size() > 0);
    }

    @Test
    public void test2(){
        departmentDao.deleteAll();
    }

}
