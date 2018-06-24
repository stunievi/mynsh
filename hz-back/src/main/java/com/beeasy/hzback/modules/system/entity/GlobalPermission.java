package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import com.beeasy.hzback.core.helper.JSONConverter;
import jdk.nashorn.internal.objects.Global;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.*;

/**
 * 全局授权表
 */
@Getter
@Setter
@Entity
@Table(name = "t_global_permission")
public class GlobalPermission extends AbstractBaseEntity{

    @Id
    @GeneratedValue
    Long id;

    //授权类型
    @Enumerated
    Type type;

    //授权对象ID
    Long objectId;

    //授权者类型
    @Enumerated
    UserType userType;

    //授权者关联ID
    Long linkId;

    //授权详情说明
    @Column(columnDefinition = JSONConverter.type)
    @Convert(converter = JSONConverter.class)
    Object description;

    /**
     * 授权类型
     */
    public enum Type{
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
        USER_METHOD


    }

    /**
     * 授权者类型
     */
    public enum UserType{
        //按部门授权
        DEPARTMENT,
        //按岗位授权
        QUARTER,
        //按人员授权
        USER
    }


}
