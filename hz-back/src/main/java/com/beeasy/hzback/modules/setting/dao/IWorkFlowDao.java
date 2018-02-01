package com.beeasy.hzback.modules.setting.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.helper.SpringContextUtils;
import com.beeasy.hzback.modules.setting.entity.Department;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.setting.entity.Work;
import com.beeasy.hzback.modules.setting.entity.WorkFlow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface IWorkFlowDao extends JpaRepository<WorkFlow, Integer> {

    /**
     * 部署业务，需检查
     * 1.是否该部门已经部署过同名业务，如果已经部署过，则视为版本升级，需要同时存在两个不同的业务版本
     * 2.业务每个节点需要涉及到的人员，必须
     * 3.业务归属于一个部门，这个部门必须
     */
    default boolean deployWork(
            Integer departmentId,
            Integer workId,
            String workflowNodeList
    ) {
        IDepartmentDao departmentDao = (IDepartmentDao) SpringContextUtils.getBean(IDepartmentDao.class);
        IWorkDao workDao = (IWorkDao) SpringContextUtils.getBean(IWorkDao.class);
        if (departmentId == null) return false;
        if (workId == null) return false;

        Department department = departmentDao.findOne(departmentId);
        if (department == null) return false;
        Work work = workDao.findOne(workId);
        if (work == null) {
            return false;
        }
        JSONArray userIds = JSON.parseArray(workflowNodeList);
        if(userIds == null){
            return false;
        }
        //得到该部门的所有人员
        Set<User> users = department.getUsers();
        //检查所有用户是否合法
        for(Object item : userIds){
            JSONObject it = (JSONObject) item;
            Integer userId = it.toJavaObject(Integer.class);
            
        }

        return true;
    }
}
