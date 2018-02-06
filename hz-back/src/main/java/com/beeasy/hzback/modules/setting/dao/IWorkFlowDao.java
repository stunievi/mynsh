package com.beeasy.hzback.modules.setting.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.helper.SpringContextUtils;
import com.beeasy.hzback.modules.setting.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface IWorkFlowDao extends JpaRepository<WorkFlow, Integer> {

    Set<WorkFlow> findByName(String name);

    /**
     * 部署业务，需检查
     * 1.是否该部门已经部署过同名业务，如果已经部署过，则视为版本升级，需要同时存在两个不同的业务版本
     * 2.业务每个节点需要涉及到的角色，必须
     * 3.业务归属于一个部门，这个部门必须
     */
    default boolean deployWork(
            Integer departmentId,
            Integer workId,
            String workflowNodeList,
            Double version
    ) {
        IDepartmentDao departmentDao = (IDepartmentDao) SpringContextUtils.getBean(IDepartmentDao.class);
        IWorkDao workDao = (IWorkDao) SpringContextUtils.getBean(IWorkDao.class);
        if (departmentId == null) return false;
        if (workId == null) return false;
        if(version == null){
            version = 1.0;
        }

        Department department = departmentDao.findOne(departmentId);
        if (department == null) return false;
        Work work = workDao.findOne(workId);
        if (work == null) {
            return false;
        }
        JSONArray roleIdsList = JSON.parseArray(workflowNodeList);
        if(roleIdsList == null){
            return false;
        }
        //检查所选的角色是否合法
        List<Set<Integer>> list = roleIdsList.stream().map(l -> ((JSONArray)l).stream().map(str -> (Integer.valueOf((String)str))).collect(Collectors.toSet())).collect(Collectors.toList());

        //检查该部门是否已经有相同的业务流程
        Set<WorkFlow> existWorkflow = this.findByName(work.getName());
        if(existWorkflow.size() > 0){
            Double finalVersion = version;
            if(existWorkflow.stream().anyMatch(item -> item.getVersion().equals(finalVersion))){
                return false;
            }
        }

        WorkFlow workFlow = new WorkFlow();
        workFlow.setDepartment(department);
        workFlow.setModel(work.getNodeList());
        workFlow.setName(work.getName());
        workFlow.setDealers(list);
        workFlow.setVersion(version);

        this.save(workFlow);

        return true;
    }


    /**
     * 发布一个工作流新实体
     * 通常，进行如此操作的情况下会填写第一个节点
     */
    default void pubNewEntity(){

    }
}
