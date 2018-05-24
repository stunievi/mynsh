package com.beeasy.hzback;

import bin.leblanc.faker.Faker;
import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.modules.system.dao.*;
import com.beeasy.hzback.modules.system.entity.*;
import com.beeasy.hzback.modules.system.form.*;
import com.beeasy.hzback.modules.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestAsync {

    @Autowired
    EntityManager entityManager;
    @Autowired
    UserService userService;
    @Autowired
    MessageService messageService;
    @Autowired
    IUserDao userDao;
    @Autowired
    WorkflowService workflowService;
    @Autowired
    IWorkflowModelDao workflowModelDao;
    @Autowired
    IDepartmentDao departmentDao;
    @Autowired
    QuartersService quartersService;

    @Test
    public void createWorkflows() {
        workflowModelDao.deleteAll();
        //列出4个版本
        for (int i = 0; i < 4; i++) {
            String[] modelNames = {"菜单权限申请","资料收集"};
            for (String modelName : modelNames) {
                WorkflowModelAdd edit = new WorkflowModelAdd();
                edit.setName(modelName);
                edit.setVersion(BigDecimal.valueOf(1.0 + i * 0.01));
                Result<WorkflowModel> result = workflowService.createWorkflow(modelName, edit);

                WorkflowModel workflowModel = result.getData();
                //附加人员
                List<Department> departments = departmentDao.findAllByName("部门1");
                Assert.assertTrue(departments.size() > 0);
                Department department = departments.get(0);

                String[] qnames = {"岗位1", "岗位2", "岗位3", "岗位4"};

                List<WorkflowQuartersEdit> list = new ArrayList<>();
                workflowModel.getNodeModels().forEach((v) -> {
                    WorkflowQuartersEdit qedit = new WorkflowQuartersEdit();
                    qedit.setNodeId(v.getId());
                    qedit.setName(v.getName());
                    for (String qname : qnames) {
                        Quarters quarters = quartersDao.findFirstByDepartmentAndName(department, qname);
                        qedit.getMainQuarters().add(quarters.getId());
                    }
                    list.add(qedit);
                });
                WorkflowQuartersEdit[] edits = new WorkflowQuartersEdit[list.size()];
                edits = list.toArray(edits);
                workflowService.setPersons(edits);

                //打开工作流
                workflowService.editWorkflowModel(workflowModel.getId(), "", true);
            }
        }
    }


    @Autowired
    DepartmentService departmentService;
    @Autowired
    IQuartersDao quartersDao;
    @Autowired
    ISystemFileDao systemFileDao;


    @Test
    public void createAdmin() throws RestException {
        User user = userDao.findByUsername("1");
        if (user != null) {
            userDao.delete(user);
        }
        UserAdd add = new UserAdd();
        add.setPhone(Faker.getPhone());
        add.setUsername("1");
        add.setPassword("2");
        add.setTrueName("管理员");
//        add.setTrueName(Faker);
        add.setBaned(false);
        userService.createUser(add);


    }

    @Test
    public void createDepartments() throws RestException {
        systemFileDao.deleteAll();
        userDao.clearUsers("1");
        createAdmin();
        departmentDao.deleteAll();
        quartersDao.deleteAll();
        String[] departments = {"部门1", "部门2", "部门3", "部门4"};
        String[] quarters = {"岗位1", "岗位2", "岗位3", "岗位4"};
        for (String department : departments) {
            DepartmentAdd departmentAdd = new DepartmentAdd();
            departmentAdd.setName(department);
            Result<Department> result = departmentService.createDepartment(departmentAdd);

            Assert.assertTrue(result.isSuccess());
            for (String quarter : quarters) {
                QuartersAdd quartersAdd = new QuartersAdd();
                quartersAdd.setDepartmentId(result.getData().getId());
                quartersAdd.setName(quarter);
                Result<Quarters> ret = userService.createQuarters(quartersAdd);
                Assert.assertTrue(ret.isSuccess());
                for (int i = 0; i < 10; i++) {
                    UserAdd userAdd = new UserAdd();
                    userAdd.setPhone(Faker.getPhone());
                    userAdd.setTrueName(Faker.getTrueName());
                    userAdd.setUsername(Faker.getName());
                    userAdd.setPassword("2");
                    userAdd.setBaned(false);
                    Result<User> r = userService.createUser(userAdd);
                    Assert.assertTrue(r.isSuccess());

                    UserEdit edit = new UserEdit();
                    Set<Long> qs = new HashSet<>();
                    qs.add(ret.getData().getId());
                    edit.setQuarters(qs);
                    edit.setId(r.getData().getId());
                    userService.editUser(edit);
                }
            }
        }

    }


    @Test
    public void test2() {
        List list = workflowModelDao.getAllWorkflows();
        Assert.assertTrue(list.size() == 1);
    }

    @Test
    public void test() {
        log.info("f");
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CloudDirectoryIndex> query = cb.createQuery(CloudDirectoryIndex.class);
        Root root = query.from(CloudDirectoryIndex.class);
        query.select(root);
//        query.where(cb.equal(null,null));
        TypedQuery<CloudDirectoryIndex> q = entityManager.createQuery(query);
        List<CloudDirectoryIndex> result = q.getResultList();

        Query query2 = entityManager.createNativeQuery("SELECT * FROM t_user WHERE username = :a");
        query2.setParameter("a", "1");
        query2.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        query2.setMaxResults(10);
        List list = query2.getResultList();
        int c = 1;

    }


    @Autowired
    IWorkflowInstanceDao instanceDao;
    @Test
    public void testUserSelect(){
//        userService.findUser(559).ifPresent(user -> {
//            userService.addExternalPermission(user.getId(), UserExternalPermission.Permission.COMMON_CLOUD_DISK);
//        });

//        List s = instanceDao.findDealedWorks(Collections.singletonList(559L),new PageRequest(0,200));
//       List s = instanceDao.findTest();
//       int c = 1;
//        List s = instanceDao.findObserveredWorks(Collections.singletonList(559L),Long.MAX_VALUE);
//        int c = 1;
    }

}
