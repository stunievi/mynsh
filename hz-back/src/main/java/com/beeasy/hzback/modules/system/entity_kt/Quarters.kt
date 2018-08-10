package com.beeasy.hzback.modules.system.entity_kt

import com.alibaba.fastjson.annotation.JSONField
import com.beeasy.hzback.core.entity.AbstractBaseEntity
import com.beeasy.hzback.core.helper.SpringContextUtils
import com.beeasy.hzback.modules.system.dao.IDepartmentDao
import com.beeasy.hzback.modules.system.dao.IQuartersDao
import io.swagger.annotations.ApiModelProperty
import org.hibernate.validator.constraints.NotEmpty
import org.hibernate.validator.constraints.Range
import java.util.ArrayList
import javax.persistence.*
import javax.validation.constraints.AssertFalse
import javax.validation.constraints.AssertTrue

@Entity
@Table(name = "t_quarters")
class Quarters : AbstractBaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @JSONField(serialize = false)
    @ManyToOne
    @JoinColumn(name = "department_id", insertable = false, updatable = false)
    var department: Department? = null
    @Column(name = "department_id")
    var departmentId: Long? = null

    var name: String = ""
    var dName: String = ""

    var info: String = ""

    @JSONField(serialize = false)
    @ManyToMany
    @JoinTable(name = "t_USER_QUARTERS", joinColumns = arrayOf(JoinColumn(name = "QUARTERS_ID", referencedColumnName = "ID")), inverseJoinColumns = arrayOf(JoinColumn(name = "USER_ID", referencedColumnName = "ID")))
    var users: List<User> = ArrayList()//    @LazyCollection(LazyCollectionOption.EXTRA)

    var code: String = ""

    //是否主管
    var manager = false

    @OrderBy(value = "DESC")
    var sort: Int = 0

}

class QuartersAddRequest {
    @ApiModelProperty(value = "岗位名称", required = true)
    @NotEmpty(message = "岗位名称不能为空")
    var name: String = ""

    @ApiModelProperty(value = "岗位所属部门", required = true)
    @Range(min = 1, message = "所属部门填写错误")
    var departmentId: Long = 0

    @ApiModelProperty(value = "岗位描述")
    var info: String = ""

    @Range(min = 0, max = 255, message = "排序在0-255之间")
    var sort: Int = 0

    var manager = false

    @AssertTrue(message = "所选部门已经有同名的岗位")
    fun isValidName(): Boolean{
        return SpringContextUtils.getBean(IDepartmentDao::class.java).countByParentIdAndName(departmentId,name) == 0
    }
}

class QuartersEditRequest {
    @Range(min = 0, message = "岗位ID错误")
    var id: Long = 0

    @NotEmpty(message = "岗位名不能为空")
    var name: String = ""

    @ApiModelProperty(value = "岗位描述")
    var info: String = ""

    //是否主管
    var manager: Boolean = false

    @Range(min = 0, max = 255, message = "排序在0-255之间")
    var sort: Int = 0


    @AssertTrue(message = "所属部门下已经有同名岗位")
    fun isValidName(): Boolean{
        return SpringContextUtils.getBean(IQuartersDao::class.java).countSameNameFromDepartment(id,name) == 0
    }

}