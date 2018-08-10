package com.beeasy.hzback.modules.system.entity_kt

import com.alibaba.fastjson.annotation.JSONField
import com.beeasy.hzback.core.entity.AbstractBaseEntity
import com.beeasy.hzback.core.helper.SpringContextUtils
import com.beeasy.hzback.modules.system.dao.IRoleDao
import org.hibernate.validator.constraints.NotEmpty
import org.hibernate.validator.constraints.Range
import java.util.ArrayList
import javax.persistence.*
import javax.validation.constraints.AssertTrue

@Entity
@Table(name = "t_role")
class Role : AbstractBaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    //角色名
    var name: String = ""

    //角色说明
    var info: String = ""

    //排序
    @OrderBy(value = "DESC")
    var sort = 0

    //是否可以删除
    var canDelete = true

    @JSONField(serialize = false)
    @ManyToMany
    @JoinTable(name = "t_user_role", joinColumns = arrayOf(JoinColumn(name = "role_id", referencedColumnName = "id")), inverseJoinColumns = arrayOf(JoinColumn(name = "user_id", referencedColumnName = "id")))
    var users: List<User> = ArrayList()
}

class RoleRequest {

    @Range(min = 1, groups = arrayOf(edit::class))
    var id: Long = 0

    @NotEmpty(groups = arrayOf(edit::class, add::class))
    var name: String = ""

    var info: String = ""

    //    @NotNull(groups = {add.class,edit.class}, message = "是否删除必填")
    var canDelete = true

        @AssertTrue(message = "已经有同名的角色", groups = arrayOf(add::class, edit::class))
        fun isValidName() : Boolean {
            val roleDao = SpringContextUtils.getBean(IRoleDao::class.java)
            return roleDao.countByNameAndIdNot(name, id) == 0
        }

    @Range(min = 0, max = 255, message = "排序在0-255之间", groups = arrayOf(add::class, edit::class))
    var sort: Int = 0

    interface add
    interface edit

}

class RoleSearchRequest{
    var name = ""
}