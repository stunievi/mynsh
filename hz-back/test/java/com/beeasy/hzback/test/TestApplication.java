package com.beeasy.hzback.test;

import com.beeasy.hzback.modules.setting.dao.IUserDao;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.core.util.CrUtils;
import com.beeasy.hzback.modules.setting.service.DepartmentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestApplication {

    @Autowired
    IUserDao userDao;

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
        List<?> list = departmentService.listAsTree();
        
    }

}
