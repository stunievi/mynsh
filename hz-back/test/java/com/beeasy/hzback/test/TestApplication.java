package com.beeasy.hzback.test;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.helper.SpringContextUtils;
//import com.beeasy.hzback.lib.zed.ClassScanner;
//import com.beeasy.hzback.lib.zed.ScanPackageTest;
import com.beeasy.hzback.lib.zed.JPAUtil;
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
import com.beeasy.hzback.modules.setting.entity.WorkNode;
import com.beeasy.hzback.modules.setting.service.DepartmentService;
import com.beeasy.hzback.modules.setting.service.UserService;
import org.hibernate.jpa.internal.metamodel.SingularAttributeImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import java.lang.reflect.Field;
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
    public void initUser(){
        User u = new User();
        u.setUsername("1");
        u.setPassword(CrUtils.md5("1".getBytes()));
        userDao.save(u);
    }

    @Autowired
    JPAUtil jpaUtil;

    @Test
    public void testmultipulSelect(){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery();
        Root root = query.from(Department.class);
        Join a = root.join("parent");
        Join b = root.join("departments");
        query.where(cb.equal(root.get("id"),1));
//        query.multiselect(a,b);
//        query.select(b);
        query.multiselect(root,a);
//        root.fetch().
        TypedQuery result = entityManager.createQuery(query);
        List list = result.getResultList();
        int aa = 1;
    }

    @Test
    public void testsql() throws Exception {

//    Root root =
//        entityManager.getCriteriaBuilder().createQuery(WorkNode.class).from(WorkNode.class);
//    String idName = jpaUtil.getIdName(root);
//        System.out.println(idName);
//        Set<Attribute> attributes = root.getModel().getDeclaredAttributes();
//        for(Attribute attribute : attributes){
//            if(attribute instanceof SingularAttributeImpl){
//                boolean b =  ((SingularAttributeImpl) attribute).isId();
//
//                System.out.println(b);
//            }
//            System.out.println(attribute.getClass().getName());;
//            String name = attribute.getDeclaringType().getAttribute("id").getName();
//            System.out.println(name);
//            Field field = attribute.getClass().getField("isIdentifier");
//            field.setAccessible(true);
//            boolean b = field.getBoolean(attribute);
//            System.out.println(b);
//        }
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
                "\t\"User\":{\n" +
                "\t\t\"roles\":{\"$rows\":1},\n" +
                "\t\t\"$where\":{\n" +
                "\t\t\t\"id\":1\n" +
                "\t\t}\n" +
                "\t}\n" +
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
