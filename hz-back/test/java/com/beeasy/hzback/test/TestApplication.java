package com.beeasy.hzback.test;

import com.beeasy.hzback.core.helper.SpringContextUtils;
import com.beeasy.hzback.modules.setting.dao.IDepartmentDao;
import com.beeasy.hzback.modules.setting.dao.IUserDao;
import com.beeasy.hzback.modules.setting.dao.IWorkDao;
import com.beeasy.hzback.modules.setting.dao.IWorkNodeDao;
import com.beeasy.hzback.modules.setting.entity.Department;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.core.util.CrUtils;
import com.beeasy.hzback.modules.setting.entity.Work;
import com.beeasy.hzback.modules.setting.entity.WorkNode;
import com.beeasy.hzback.modules.setting.service.DepartmentService;
import com.beeasy.hzback.modules.setting.service.UserService;
import org.hibernate.Hibernate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.transaction.Transactional;
import java.util.ArrayList;
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

    @Autowired
    UserService userService;

    @Autowired
    IWorkNodeDao workNodeDao;
    @Autowired
    IWorkDao workDao;

    @Test
    public void addAdminUser(){
        User user = new User();
        user.setUsername("1");
        user.setPassword("2");
        userService.add(user);
    }
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
    public void testWorkdNode(){
        Work work = workDao.findOne(1);
        WorkNode workNode = new WorkNode();
        List list = new ArrayList<WorkNode.Node>();
        WorkNode.Node n = new WorkNode.Node();
        n.test = "fuck";
        list.add(n);
        workNode.setNode(list);
        workNode.setPosition(0);
        workNode.setWork(work);
        workNodeDao.save(workNode);
    }

    @Test
    public void test(){
        Assert.assertNotNull(SpringContextUtils.getContext());
        List<Work> list = workDao.findAll();


        Department top = departmentDao.findByName("cubi");
//        Set<?> set = top.getDepartments();
//        System.out.println(set.size());
    }

}
