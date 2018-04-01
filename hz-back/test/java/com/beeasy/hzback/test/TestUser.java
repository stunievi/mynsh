package com.beeasy.hzback.test;

import bin.leblanc.faker.Faker;
import com.alibaba.fastjson.JSONArray;
import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.modules.setting.dao.IUserDao;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.system.form.UserAdd;
import com.beeasy.hzback.modules.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestUser {

    @Autowired
    UserService userService;
    @Autowired
    IUserDao userDao;


    @Test
    public void test() throws RestException {
        User u = null;
        try {
            //adduser
            UserAdd userAdd = new UserAdd();
            userAdd.setUsername(Faker.getName());
            userAdd.setPassword(Faker.getName());
            userAdd.setPhone(Faker.getPhone());
            u = userService.createUser(userAdd);

            assertTrue(u.getId() > 0);

            //unbind menu
            u = userDao.findOne(u.getId());
            userService.unbindMenus(u, Arrays.asList("工作台"));
            JSONArray menu = userService.getMenus(u);
            assertTrue(menu.size() > 0);
            assertNotEquals(menu.getJSONObject(0).getString("name"), "工作台");

        }catch (RestException e){

        }
        finally {
            //clear
            userService.deleteUser(u);
        }

        log.info("finished");

    }

    @Test
    public void fuck(){

    }
}
