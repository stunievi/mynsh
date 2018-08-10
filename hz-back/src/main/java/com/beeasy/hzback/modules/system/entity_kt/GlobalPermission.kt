package com.beeasy.hzback.modules.system.entity_kt

import com.beeasy.hzback.core.entity.AbstractBaseEntity
import com.beeasy.hzback.core.helper.JSONConverter
import javax.persistence.*



/**
 * 全局授权表
 */
@Entity
@Table(name = "t_global_permission", indexes = arrayOf(Index(name = "objectId", columnList = "objectId"), Index(name = "type", columnList = "type"), Index(name = "userType", columnList = "userType"), Index(name = "linkId", columnList = "linkId"), Index(name = "oid_type", columnList = "objectId,type"), Index(name = "lid_utype", columnList = "linkId,userType")))
class GlobalPermission : AbstractBaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    //授权类型
    @Enumerated(value = EnumType.STRING)
    lateinit var type: Type

    //授权对象ID
    var objectId: Long? = null

    //授权者类型
    @Enumerated(value = EnumType.STRING)
    lateinit var userType: UserType

    //授权者关联ID
    var linkId: Long? = null

    //授权详情说明
    @Column(columnDefinition = JSONConverter.type)
    @Convert(converter = JSONConverter::class)
    var description: Any? = null

    /**
     * 授权类型
     */
    enum class Type {
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
    enum class UserType {
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
