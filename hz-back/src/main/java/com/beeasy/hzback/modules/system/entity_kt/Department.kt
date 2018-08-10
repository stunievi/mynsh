package com.beeasy.hzback.modules.system.entity_kt

import com.alibaba.fastjson.annotation.JSONField
import com.beeasy.hzback.core.entity.AbstractBaseEntity
import com.beeasy.hzback.core.helper.SpringContextUtils
import com.beeasy.hzback.modules.system.dao.IDepartmentDao
import io.swagger.annotations.ApiModelProperty
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.validator.constraints.NotEmpty
import org.hibernate.validator.constraints.Range
import org.springframework.data.annotation.CreatedDate
import java.util.*
import javax.persistence.*
import javax.validation.constraints.AssertTrue
import javax.validation.constraints.Min


@Entity
@Table(name = "t_department")
class Department : AbstractBaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var name: String = ""
    var info: String = ""


    @NotFound(action = NotFoundAction.IGNORE)
    @JSONField(serialize = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    var parent: Department? = null

    @Column(name = "parent_id")
    var parentId: Long? = null

    @CreatedDate
    var addTime: Date? = null

    @OrderBy(value = "sort ASC, id ASC")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    var children : MutableList<Department> = mutableListOf()

    @OneToMany(mappedBy = "department")
    var quarters: MutableList<Quarters> = mutableListOf()

    //部门编号
    var code: String = ""

    var sort: Int = 0

    var accCode: String = ""
    var deleted = false

    //    @Transient
    //    public List<User> getUsers(){
    //        return getQuarters()
    //                .stream()
    //                .map(q -> q.getUsers())
    //                .flatMap(Set::stream)
    //                .distinct()
    //                .collect(Collectors.toList());
    //    }

    //    @Transient
    //    public Long getParentId(){
    //        return getParent() == null ? 0 : getParent().getId();
    //    }


}

class DepartmentAdd {
    @NotEmpty(message = "部门名称不能为空")
    var name: String = ""

    @Min(value = 0, message = "父级ID不能小于0")
    var parentId: Long = 0

    var info: String = ""

    @Range(min = 0, max = 255)
    var sort = 0

    var accCode = ""

    @AssertTrue(message = "已经有同名部门")
    fun isValidDepartment(): Boolean {
        return if (0L == parentId) {
            SpringContextUtils.getBean(IDepartmentDao::class.java).countByParentAndName(null, name) == 0
        } else {
            SpringContextUtils.getBean(IDepartmentDao::class.java).countByParentIdAndName(parentId, name) == 0
        }
    }
}


class DepartmentEdit {

    @ApiModelProperty(value = "部门ID")
    @Range(min = 1)
    var id: Long = 0

    @ApiModelProperty(value = "部门名称")
    var name = ""

//    @ApiModelProperty(value = "部门父级ID,顶级部门请写0")
//    @Range(value = 0, message = "父级ID不能小于0")
//    val parentId: Long

    @ApiModelProperty(value = "部门描述")
    var info = ""

    @Range(min = 0, max = 255, message = "排序在0-255之间")
    var sort: Int = 0

    var accCode = ""

    @AssertTrue(message = "已经存在同名部门")
    fun isValidDepartment(): Boolean {
        val dao = SpringContextUtils.getBean(IDepartmentDao::class.java)
        val department = dao.findById(id).orElse(null) ?: return false
        return dao.countByParentIdAndNameAndIdNot(department.parentId, name, department.id!!) == 0
    }

}
