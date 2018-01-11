package com.beeasy.hzback.test;

import com.beeasy.hzback.modules.setting.dao.IDepartmentDao;
import com.beeasy.hzback.modules.setting.dao.IUserDao;
import com.beeasy.hzback.modules.setting.entity.Department;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.core.util.CrUtils;
import com.beeasy.hzback.modules.setting.service.DepartmentService;
import org.hibernate.Hibernate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestApplication {

    @Autowired
    IUserDao userDao;

    @Autowired
    IDepartmentDao departmentDao;

    @Autowired
    DepartmentService departmentService;

    @Test
    public void contextLoads() {
        User user = new User();
        user.setUsername("cubige");
        user.setPassword(CrUtils.md5("123456".getBytes()));
        userDao.save(user);

    }

    @Test
    public void listAllDepartment(){
        Department top = new Department();
        top.setName("cubi");
        departmentDao.save(top);

        for(int i = 0; i < 3; i++){
            Department item = new Department();
            item.setName(i + "");
            item.setParent(top);
            departmentDao.save(item);
        }
        departmentDao.save(top);


    }

    @Test
    public void test(){
        Department top = departmentDao.findByName("cubi");
        Set<?> set = top.getDepartments();
        System.out.println(set.size());
    }

}
