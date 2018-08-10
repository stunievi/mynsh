package com.beeasy.hzback;

import bin.leblanc.faker.Faker;
import com.beeasy.hzback.modules.system.dao.IDepartmentDao;
import com.beeasy.hzback.modules.system.entity_kt.Department;
//import com.beeasy.hzback.modules.system.form.DepartmentAdd;
import com.beeasy.hzback.modules.system.entity_kt.DepartmentAdd;
import com.beeasy.hzback.modules.system.entity_kt.DepartmentEdit;
import com.beeasy.hzback.modules.system.service.DepartmentService;
import com.beeasy.hzback.modules.system.service_kt.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestDepartment {

    @Autowired
    IDepartmentDao departmentDao;
    @Autowired
    DepartmentService departmentService;
    @Autowired
    UserService userService;


    private Department createDepartment(Department parent){
        DepartmentAdd departmentAdd = new DepartmentAdd();
        departmentAdd.setName(Faker.getName());
        departmentAdd.setInfo(Faker.getName());
        departmentAdd.setParentId(parent == null ? null : parent.getId());
        Department department = userService.createDepartment(departmentAdd);
        return department;
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
        Department department = userService.editDepartment(edit);

        //edit failed
        edit = new DepartmentEdit();
        edit.setId(parent.getId());
        edit.setName(Faker.getName());
        department = userService.editDepartment(edit);
        assertEquals(department.getName(), edit.getName());

        //can not edit parent
//        Department newChild = createDepartment(parent);
//        edit = new DepartmentEdit();
//        edit.setId(child.getId());
//        edit.setParentId(newChild.getId());
//        try{
//
//        }
//        catch (RestException e){
//            assertNotNull(e);
//        }
//        result = departmentService.editDepartment(edit);
//        assertFalse(result.isSuccess());

        //cannot delete
        boolean b = userService.deleteDepartment(parent.getId());
        assertTrue(b);

        //find
//        List<Department> departments = departmentService.findDepartments(null,null);
//        assertTrue(departments.size() > 0);
    }

    @Test
    public void test2(){
        departmentDao.deleteAll();
    }

}
