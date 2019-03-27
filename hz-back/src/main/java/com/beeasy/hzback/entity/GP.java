package com.beeasy.hzback.entity;

import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;

@Table(name = "T_GLOBAL_PERMISSION")
@Getter
@Setter
public class GP {
    @AssignID("simple")
    Long id;
    Type type;
    Long objectId;
//    UserType userType;
//    Long linkId;
    String description;

    String k1;
    String k2;
    Long oid;

    /**
     * 授权类型
     */
    public enum Type {
        //共享文件柜操作
        COMMON_CLOUD_DISK,

        //允许观察工作流
        WORKFLOW_OBSERVER,
        //允许指派工作流
//        WORKFLOW_POINTER,
        //允许发起工作流
        WORKFLOW_PUB,

        //业务主办
        WORKFLOW_MAIN_QUARTER,
        //业务协办
        WORKFLOW_SUPPORT_QUARTER,

        //用户权限授权
        USER_METHOD,

        //数据查询约束
        DATA_SEARCH_CONDITION,
        //数据查询结果
        DATA_SEARCH_RESULT
    }

    /**
     * 授权者类型
     */
    public enum UserType {
        //按部门授权
        DEPARTMENT,
        //按岗位授权
        QUARTER,
        //按人员授权
        USER,
        //按角色授权
        ROLE
    }
}
