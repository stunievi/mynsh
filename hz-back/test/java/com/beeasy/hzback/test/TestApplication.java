package com.beeasy.hzback.test;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.helper.SpringContextUtils;
//import com.beeasy.hzback.lib.zed.ClassScanner;
//import com.beeasy.hzback.lib.zed.ScanPackageTest;
import com.beeasy.hzback.lib.zed.Zed;
import com.beeasy.hzback.modules.setting.dao.IDepartmentDao;
import com.beeasy.hzback.modules.setting.dao.IUserDao;
import com.beeasy.hzback.modules.setting.dao.IWorkDao;
//import com.beeasy.hzback.modules.setting.dao.IWorkNodeDao;
import com.beeasy.hzback.modules.setting.entity.Department;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.core.util.CrUtils;
import com.beeasy.hzback.modules.setting.entity.Work;
//import com.beeasy.hzback.modules.setting.entity.WorkNode;
import com.beeasy.hzback.modules.setting.service.DepartmentService;
import com.beeasy.hzback.modules.setting.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
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

//    @Autowired
//    IWorkNodeDao workNodeDao;
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
//        Work work = workDao.findOne(1);
//        WorkNode workNode = new WorkNode();
//        List list = new ArrayList<WorkNode.Node>();
//        WorkNode.Node n = new WorkNode.Node();
//        n.test = "fuck";
//        list.add(n);
//        WorkNode workNode = new WorkNode();
////        workNode.setType("ri");
//        workNode.setData(new ShenheNode());
//        workNodeDao.save(workNode);

//        workNode.setNode(list);
//        workNode.setPosition(0);
//        workNode.setWork(work);
//        workNodeDao.save(workNode);
    }

    @Test
    public void test(){
        Assert.assertNotNull(SpringContextUtils.getContext());
        List<Work> list = workDao.findAll();


        Department top = departmentDao.findByName("cubi");
//        Set<?> set = top.getDepartments();
//        System.out.println(set.size());
    }

    @Autowired
    private EntityManager entityManager;

    @Autowired
    Zed zed;

    @Test
    public void testsql() throws Exception {

        zed.init();

//        Set<?> set = ClassScanner.getClasses("com.beeasy");
//        Set<?> set = ScanPackageTest.findPackageAnnotationClass("com.beeasy.hzback", Entity.class);
//        String sql = "select now()";
//        CriteriaBuilder builder = entityManagerFactory.getNativeEntityManagerFactory().getCriteriaBuilder();
//        CriteriaQuery<Object> query = builder.createQuery();
//        query.from()
//                entityManagerFactory
//        builder.and()

//        EntityManager em = entityManagerFactory.getNativeEntityManagerFactory().createEntityManager();
//        em.createQuery(query);
//        Query query = em.createNativeQuery(sql);
//        List<?> result = query.getResultList();

        String testStr = "{\n" +
                "    \"User\": {\n" +
                "         \"id\":1000,\n" +
                "\n" +
                "          \"or\": {\n" +
                "               \"id\":1\n" +
                "\n" +
                "\n" +
                "\n" +
                "             }\n" +
                "    },\n" +
                "   \"Work\":{\"name\":1}\n" +
                "}";

        (zed).parse(testStr);


//        JSONObject obj = new JSONObject();
//        JSONObject user = new JSONObject();
//        user.put("id",1);
//        obj.put("User",user);

//        zed.parseGet(obj);

//        Zed.register(User.class);
    }

}
