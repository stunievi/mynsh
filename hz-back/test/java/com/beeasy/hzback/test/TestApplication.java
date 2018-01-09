package com.beeasy.hzback.test;

import com.beeasy.hzback.modules.setting.dao.IUserDao;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.util.CrUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestApplication {

    @Autowired
    IUserDao userDao;

    @Test
    public void contextLoads() {
        User user = new User();
        user.setUsername("cubige");
        user.setPassword(CrUtils.md5("123456".getBytes()));
        userDao.save(user);

    }

}
