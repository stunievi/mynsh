package com.beeasy.hzback.test;

import bin.leblanc.faker.Faker;
import com.alibaba.fastjson.JSONArray;
import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.modules.setting.dao.IUserDao;
import com.beeasy.hzback.modules.setting.entity.Department;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.system.entity.Quarters;
import com.beeasy.hzback.modules.system.form.DepartmentAdd;
import com.beeasy.hzback.modules.system.form.QuartersAdd;
import com.beeasy.hzback.modules.system.form.UserAdd;
import com.beeasy.hzback.modules.system.service.DepartmentService;
import com.beeasy.hzback.modules.system.service.QuartersService;
import com.beeasy.hzback.modules.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.junit.Assert.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestUser {

    @Autowired
    UserService userService;
    @Autowired
    IUserDao userDao;

    @Autowired
    DepartmentService departmentService;
    @Autowired
    QuartersService quartersService;


    @Test
    public void test() throws RestException {
        User u = null;
        Department department = null;
        Quarters quarters = null;
        try {
            //创建用户
            UserAdd userAdd = new UserAdd();
            userAdd.setUsername(Faker.getName());
            userAdd.setPassword(Faker.getName());
            userAdd.setPhone(Faker.getPhone());
            u = userService.createUser(userAdd);

            assertNotNull(u);
            assertTrue(u.getId() > 0);
            assertNotNull(u.getMenuPermission());

            //unbind menu
            //限制菜单
            userService.unbindMenus(u.getId(), Arrays.asList("工作台"));
            JSONArray menu = userService.getMenus(u.getId());
            assertTrue(menu.size() > 0);
            assertNotEquals(menu.getJSONObject(0).getString("name"), "工作台");

            //创建部门
            DepartmentAdd departmentAdd = new DepartmentAdd();
            departmentAdd.setName(Faker.getName());
            departmentAdd.setInfo("");
            department = departmentService.createDepartment(departmentAdd);

            assertNotNull(department);
            assertTrue(department.getId() > 0);

            //创建角色
            QuartersAdd quartersAdd = new QuartersAdd();
            quartersAdd.setName(Faker.getName());
            quartersAdd.setDepartmentId(department.getId());
            quarters = quartersService.createQuarters(quartersAdd);
            assertNotNull(quarters);
            assertNotNull(quarters.getId() > 0);

            //绑定角色
            userService.addQuarters(u.getId(),quarters.getId());

            //删除角色
            userService.removeQuarters(u.getId(), quarters.getId());
            u = userService.find(u.getId());
            assertTrue(u.getQuarters().size() == 0);


            //添加工作流



        }catch (RestException e){

        }
        finally {
            //clear
            userService.deleteUser(u.getId());
//            departmentService.deleteDepartment(department.getId());

        }

        log.info("finished");

    }


    @Test
    public void fuck(){

    }
}
