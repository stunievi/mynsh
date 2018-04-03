package com.beeasy.hzback.test;

import bin.leblanc.faker.Faker;
import com.alibaba.fastjson.JSONArray;
import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.modules.setting.dao.IUserDao;
import com.beeasy.hzback.modules.setting.entity.Department;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.system.entity.Quarters;
import com.beeasy.hzback.modules.system.entity.WorkflowInstance;
import com.beeasy.hzback.modules.system.entity.WorkflowModel;
import com.beeasy.hzback.modules.system.form.*;
import com.beeasy.hzback.modules.system.service.DepartmentService;
import com.beeasy.hzback.modules.system.service.QuartersService;
import com.beeasy.hzback.modules.system.service.UserService;
import com.beeasy.hzback.modules.system.service.WorkflowService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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


    @Test
    public void test() throws RestException {
        User u = null;
        Department department = null;
        Quarters quarters = null;
        WorkflowModel workflowModel = null;
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
            u = userService.unbindMenus(u.getId(), Arrays.asList("工作台"));
            assertNotNull(u);

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
            assertTrue(quarters.getId() > 0);

            //绑定角色
            u = userService.addQuarters(u.getId(),quarters.getId());
            assertNotNull(u);

            //删除角色
            u = userService.removeQuarters(u.getId(), quarters.getId());
            assertNotNull(u);
            assertTrue(u.getQuarters().size() == 0);

            //重新绑定
            userService.setQuarters(u.getId(), quarters.getId());
            u = userService.find(u.getId());
            assertTrue(u.getQuarters().size() > 0);


            //添加工作流
            WorkflowModelAdd modelAdd = new WorkflowModelAdd();
            modelAdd.setName(Faker.getName());
            modelAdd.setInfo("");
            modelAdd.setVersion(BigDecimal.valueOf(1.0));
            workflowModel = workflowService.createWorkflow("资料收集",modelAdd);
            assertNotNull(workflowModel);

            //绑定工作流岗位
            WorkflowQuartersEdit edit = new WorkflowQuartersEdit();
            edit.setName("资料收集");
            edit.getMainQuarters().add(quarters.getId());
            workflowService.setPersons(workflowModel.getId(),edit);

            WorkflowModel fm = workflowService.findModel(workflowModel.getId());
            assertTrue(fm.getPersons().size() > 0);
            assertTrue(fm.getPersons().get(0).getUid() > 0);

            //打开工作流
            workflowService.setOpen(workflowModel.getId(),true);

            //发起工作流
            WorkflowInstance instance = workflowService.startNewInstance(u.getId(),workflowModel.getId());
            assertNotNull(instance);
            assertTrue(instance.getId() > 0);

            //提交处理数据
            Map<String,String> data = new HashMap<>();
            data.put("clientName",Faker.getName());
            data.put("manager",Faker.getName());
            instance = workflowService.submitData(u.getId(),instance.getId(),data);
            instance = workflowService.submitData(u.getId(),instance.getId(),data);
            assertNotNull(instance);
            assertTrue(instance.getNodeList().size() > 0);
            assertTrue(instance.getNodeList().get(0).getAttributeList().size() < 3);

            //处理下一步
            instance = workflowService.goNext(u.getId(),instance.getId());
            assertNotNull(instance);
            assertEquals(instance.getCurrentNode().getNodeName(),("是否拒贷"));

            //提交审核数据
            String ret = "是";
            instance = workflowService.submitData(u.getId(),instance.getId(),ret);
            assertNotNull(instance);


            //检查流程是否结束
            assertTrue(instance.isFinished());

            //测试完成
            log.info("workflow finished");


        }catch (RestException e){
            e.printStackTrace();
        }
        finally {
            //clear
            boolean flag;
            flag = userService.deleteUser(u.getId());
            assertTrue(flag);
            flag = workflowService.deleteWorkflow(workflowModel.getId(),true);
            assertTrue(flag);
//            departmentService.deleteDepartment(department.getId());

        }

        log.info("all finished");

    }


    @Test
    public void fuck(){

    }
}
