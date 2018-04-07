package com.beeasy.hzback.test;

import bin.leblanc.faker.Faker;
import com.alibaba.fastjson.JSONArray;
import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.modules.exception.CannotFindEntityException;
import com.beeasy.hzback.modules.setting.dao.IDepartmentDao;
import com.beeasy.hzback.modules.setting.dao.IUserDao;
import com.beeasy.hzback.modules.setting.entity.Department;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.system.cache.SystemConfigCache;
import com.beeasy.hzback.modules.system.dao.IInspectTaskDao;
import com.beeasy.hzback.modules.system.dao.IWorkflowModelDao;
import com.beeasy.hzback.modules.system.entity.InspectTask;
import com.beeasy.hzback.modules.system.entity.Quarters;
import com.beeasy.hzback.modules.system.entity.WorkflowInstance;
import com.beeasy.hzback.modules.system.entity.WorkflowModel;
import com.beeasy.hzback.modules.system.form.*;
import com.beeasy.hzback.modules.system.service.DepartmentService;
import com.beeasy.hzback.modules.system.service.QuartersService;
import com.beeasy.hzback.modules.system.service.UserService;
import com.beeasy.hzback.modules.system.service.WorkflowService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import java.math.BigDecimal;
import java.util.*;

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
    @Autowired
    WorkflowService workflowService;
    @Autowired
    ScriptEngine engine;
    @Autowired
    ScriptContext context;
    @Autowired
    SystemConfigCache cache;
    @Autowired
    RedisTemplate redisTemplate;

    User u = null;
    Department department = null;
    WorkflowModel workflowModel = null;

    private Set<Long> departments = new HashSet<>();
    private Set<Long> users = new HashSet<>();
    private Set<Long> quarterss = new HashSet<>();
    private Set<Long> workflows = new HashSet<>();
    private Set<Long> workflowModels = new HashSet<>();

    public void testgo() {
        log.info("fuck go");
    }

    @Test
    public void prepare() throws RestException {

        //创建用户
        UserAdd userAdd = new UserAdd();
        userAdd.setUsername(Faker.getName());
        userAdd.setPassword(Faker.getName());
        userAdd.setPhone(Faker.getPhone());
        u = userService.createUser(userAdd);
        assertTrue(u.getId() > 0);
        users.add(u.getId());

        //unbind menu
        //限制菜单
        u = userService.unbindMenus(u.getId(), Arrays.asList("工作台"));
        assertNotNull(u);

        JSONArray menu = userService.getMenus(u.getId());
        assertTrue(menu.size() > 0);
        assertNotEquals(menu.getJSONObject(0).getString("name"), "工作台");

    }

    private Department createDepartment(String name) throws RestException {
        DepartmentAdd departmentAdd = new DepartmentAdd();
        departmentAdd.setName(Faker.getName());
        departmentAdd.setInfo("");
        department = departmentService.createDepartment(departmentAdd);
        assertNotNull(department);
        assertTrue(department.getId() > 0);
        departments.add(department.getId());
        return department;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static class Holder{
        User user;
        Department department;
        Quarters quarters;
    }

    private Quarters createQuarters(User user, Department department, String name) throws RestException {
        QuartersAdd quartersAdd = new QuartersAdd();
        quartersAdd.setName(Faker.getName());
        quartersAdd.setDepartmentId(department.getId());
        Quarters quarters = quartersService.createQuarters(quartersAdd);
        assertNotNull(quarters);
        assertTrue(quarters.getId() > 0);
        quarterss.add(quarters.getId());
        u = userService.addQuarters(user.getId(), quarters.getId());
        return quarters;
    }

    private WorkflowModel createWorkflow(String name) throws RestException {
        WorkflowModelAdd modelAdd = new WorkflowModelAdd();
        modelAdd.setName(Faker.getName());
        modelAdd.setInfo("");
        modelAdd.setVersion(BigDecimal.valueOf(1.0));
        WorkflowModel workflowModel = workflowService.createWorkflow(name, modelAdd);
        assertTrue(workflowModel.getId() > 0);
        workflowModels.add(workflowModel.getId());

//        workflowModel = createWorkflow("菜单权限申请");


        List<WorkflowQuartersEdit> list = new ArrayList<>();
        workflowModel.getModel().forEach((k,v) -> {
                WorkflowQuartersEdit edit = new WorkflowQuartersEdit();
                edit.setName(v.getName());
                edit.getMainUser().add(u.getId());
                list.add(edit);
        });
        WorkflowQuartersEdit[] edits = new WorkflowQuartersEdit[list.size()];
        edits = list.toArray(edits);

        workflowModel = workflowService.setPersons(workflowModel.getId(),edits);

        return workflowModel;
    }


    @Test
    public void test_资料收集() throws RestException {
        //创建部门
        department = createDepartment(Faker.getName());

        Quarters quarters;

        //创建绑定角色
        quarters = createQuarters(u,department,Faker.getName());

        //删除角色
        u = userService.removeQuarters(u.getId(), quarters.getId());
        assertNotNull(u);
        assertTrue(u.getQuarters().size() == 0);

        //重新绑定
        u = userService.setQuarters(u.getId(), quarters.getId());
        assertTrue(u.getQuarters().size() > 0);

        //添加工作流
        workflowModel = createWorkflow("资料收集");


        //绑定工作流岗位
        WorkflowQuartersEdit edit = new WorkflowQuartersEdit();
        edit.setName("资料收集");
        edit.getMainQuarters().add(quarters.getId());
//            workflowModel = workflowService.setPersons(workflowModel.getId(),edit);
        WorkflowQuartersEdit edit1 = new WorkflowQuartersEdit();
        edit1.setName("是否拒贷");
        edit1.getMainQuarters().add(quarters.getId());
        workflowModel = workflowService.setPersons(workflowModel.getId(), edit, edit1);

        assertTrue(workflowModel.getPersons().size() > 0);
        assertTrue(workflowModel.getPersons().get(0).getUid() > 0);

        //打开工作流
        workflowService.setOpen(workflowModel.getId(), true);
        workflowService.setOpen(workflowModel.getId(), false);

        //打开一次后就不能再删除
        boolean success = workflowService.deleteWorkflowModel(workflowModel.getId(), false);
        assertFalse(success);

        workflowService.setOpen(workflowModel.getId(), true);


        //发起工作流
        WorkflowInstance instance = workflowService.startNewInstance(u.getId(), workflowModel.getId());
        assertNotNull(instance);
        assertTrue(instance.getId() > 0);

        //提交处理数据
        Map<String, String> data = new HashMap<>();
        data.put("clientName", Faker.getName());
        data.put("manager", Faker.getName());
        instance = workflowService.submitData(u.getId(), instance.getId(), data);
        instance = workflowService.submitData(u.getId(), instance.getId(), data);
        assertNotNull(instance);
        assertTrue(instance.getNodeList().size() > 0);
        assertTrue(instance.getNodeList().get(0).getAttributeList().size() < 3);

        //处理下一步
        instance = workflowService.goNext(u.getId(), instance.getId());
        assertNotNull(instance);
        assertEquals(instance.getCurrentNode().getNodeName(), ("是否拒贷"));

        //提交审核数据
        data.clear();
        data.put("key","是");
        instance = workflowService.submitData(u.getId(), instance.getId(), data);
        assertNotNull(instance);

        //确认
        instance = workflowService.goNext(u.getId(), instance.getId());
        assertNotNull(instance);

        //检查流程是否结束
        assertTrue(instance.isFinished());

    }


    @Test
    public void test_权限申请() throws RestException {
        workflowModel = createWorkflow("菜单权限申请");
//        String[] nodes = {"发起权限申请","判断表内/表外","授信管理部审核","合规部审核","电子银行部确认","菜运营部审","总领导班子审核"};
//        WorkflowQuartersEdit[] edits = new WorkflowQuartersEdit[nodes.length];
//        int count = 0;
//        for (String node : nodes) {
//            WorkflowQuartersEdit edit = new WorkflowQuartersEdit();
//            edit.setName(node);
//            edit.getMainUser().add(u.getId());
//            edits[count++] = edit;
//        }
//        workflowModel = workflowService.setPersons(workflowModel.getId(),edits);

        assertTrue(workflowModel.getId() > 0);
        assertTrue(workflowModel.getPersons().size() > 0);

        //打开工作流
        workflowService.setOpen(workflowModel.getId(),true);

        //发起工作流
        WorkflowInstance instance = workflowService.startNewInstance(u.getId(),workflowModel.getId());
        assertTrue(instance.getId() > 0);

        //提交处理数据
        Map<String,String> data = new HashMap<>();
        data.put("type","表内");
        data.put("apply","cubi123");
        instance = workflowService.submitData(u.getId(),instance.getId(),data);
        assertTrue(instance.getNodeList().size() > 0);

        instance = workflowService.goNext(u.getId(),instance.getId());
        assertEquals(instance.getCurrentNode().getNodeName(),"判断表内/表外");
        //等待验证
        int limit = 10;
        while(true){
            if(limit-- == 0){
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            instance = workflowService.findInstance(instance.getId()).get();
            if(!instance.getCurrentNode().getNodeName().equals("判断表内/表外")){
                break;
            }
        }

        //授信部审核
        data.clear();
        data.put("key","是");
        instance = workflowService.submitData(u.getId(), instance.getId(),data);
        instance = workflowService.goNext(u.getId(),instance.getId());

        //合规部审核
        data.clear();
        data.put("key","是");
        instance = workflowService.submitData(u.getId(), instance.getId(),data);
        instance = workflowService.goNext(u.getId(),instance.getId());

        //总领导班子审核
        data.clear();
        data.put("key","是");
        instance = workflowService.submitData(u.getId(), instance.getId(),data);
        instance = workflowService.goNext(u.getId(),instance.getId());

        //运营部审核
        data.clear();
        data.put("key","是");
        instance = workflowService.submitData(u.getId(), instance.getId(),data);
        instance = workflowService.goNext(u.getId(),instance.getId());


        //电子银行部确认
        data.clear();
        data.put("key","是");
        instance = workflowService.submitData(u.getId(), instance.getId(),data);
        instance = workflowService.goNext(u.getId(),instance.getId());

        assertTrue(instance.isFinished());
        assertEquals(instance.getCurrentNode().getNodeName(),"结束");


    }

    public void test_贷后跟踪() throws RestException {
        workflowModel = createWorkflow("贷后跟踪");
        workflowModel = workflowService.setOpen(workflowModel.getId(),true);

        //系统发布任务
        InspectTask task = workflowService.createInspectTask("贷后跟踪",u.getId(),true);
        assertTrue(task.getId() > 0);

        //接受任务
        WorkflowInstance instance = workflowService.acceptInspectTask(u.getId(),task.getId());
        assertTrue(instance.getId() > 0);

        //测试查询
        Page<InspectTask> list = workflowService.getInspectTaskList(u.getId(),"",null,new PageRequest(0,100,new Sort(Sort.Direction.DESC,"id")));
        assertTrue(list.getContent().size() > 0);

        list = workflowService.getInspectTaskList(0,"",null,new PageRequest(0,100));
        assertEquals(list.getContent().size(),0);

        //处理申请
        Map data = new HashMap();
        data.put("f5",10000000 + 1);
        instance = workflowService.submitData(u.getId(),instance.getId(),data);
        instance = workflowService.goNext(u.getId(),instance.getId());
        assertEquals(instance.getCurrentNode().getNodeName(),"上传报告");

        instance = workflowService.submitData(u.getId(),instance.getId(),data);
        instance = workflowService.goNext(u.getId(),instance.getId());
        assertEquals(instance.getCurrentNode().getNodeName(),"审核签署");

        data.clear();
        data.put("key","是");
        instance = workflowService.submitData(u.getId(),instance.getId(),data);
        instance = workflowService.goNext(u.getId(),instance.getId());


        assertTrue(instance.isFinished());
    }

    public void test_催收() throws RestException {
        workflowModel = createWorkflow("催收");
        Map data = new HashMap();
        data.put("f1","123");

        WorkflowInstance instance = workflowService.startNewInstance(u.getId(),workflowModel.getId());
        assertTrue(instance.getId() > 0);

        data.clear();
        data.put("key","是");
        instance = workflowService.submitData(u.getId(),instance.getId(),data);
        instance = workflowService.goNext(u.getId(),instance.getId());


        data.clear();
        data.put("key","是");
        instance = workflowService.submitData(u.getId(),instance.getId(),data);
        instance = workflowService.goNext(u.getId(),instance.getId());

        assertEquals(instance.getCurrentNode().getNodeName(),"结束");
    }

    public void test_任务生成() throws RestException {

    }


    @Autowired
    IWorkflowModelDao modelDao;
    @Autowired
    IDepartmentDao departmentDao;
    @Autowired
    IInspectTaskDao inspectTaskDao;

    @Test
    public void clear() throws CannotFindEntityException {
        redisTemplate.delete("workflow");
        redisTemplate.delete("behavior.js");

        modelDao.deleteAll();
//        inspectTaskDao.deleteAll();

//        workflowModels.forEach(wid -> {
//            workflowService.deleteWorkflowModel(wid, true);
//        });
        users.forEach(uid -> {
            try {
                userService.deleteUser(uid);
            } catch (CannotFindEntityException e) {
                e.printStackTrace();
            }
        });

        departmentDao.deleteAll();

//        departments.forEach(did -> {
//            departmentService.deleteDepartment(did);
//        });


//        boolean flag;
//        flag = userService.deleteUser(u.getId());
//        assertTrue(flag);
//        flag = workflowService.deleteWorkflow(workflowModel.getId(),true);
//        assertTrue(flag);
    }



    @Test
    public void test() {
        try{
            prepare();
            test_资料收集();
            test_权限申请();
            test_贷后跟踪();
        }catch (RestException e){
           e.printStackTrace();
        }
        finally {
            try {
                clear();
            } catch (CannotFindEntityException e) {
                e.printStackTrace();
            }
        }
    }


}
