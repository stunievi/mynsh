package com.beeasy.hzback.modules.system.entity_kt

import com.alibaba.fastjson.annotation.JSONField
import com.beeasy.hzback.core.entity.AbstractBaseEntity
import com.beeasy.hzback.core.helper.JSONConverter
import com.beeasy.hzback.core.helper.SpringContextUtils
import com.beeasy.hzback.modules.system.dao.IUserDao
import com.beeasy.hzback.modules.system.form.Pager
import io.swagger.annotations.ApiModelProperty
import lombok.extern.slf4j.Slf4j
import org.apache.commons.lang.StringUtils
import org.hibernate.validator.constraints.Email
import org.hibernate.validator.constraints.Range
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*
import javax.validation.constraints.AssertTrue
import javax.validation.constraints.Pattern
import kotlin.collections.ArrayList


@Slf4j
@Entity
@Table(name = "t_user")
@EntityListeners(AuditingEntityListener::class)
class User : AbstractBaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(unique = true, columnDefinition = JSONConverter.VARCHAR_20)
    lateinit var username: String

    @Column(length = 50)
    @JSONField(serialize = false)
    lateinit var password: String

    @Column(columnDefinition = JSONConverter.VARCHAR_20)
    var trueName: String? = null
        set(trueName) {
            field = this.trueName
        }
    @Column(length = 20)
    var phone: String? = null
        set(phone) {
            field = this.phone
        }
    @Column(length = 50)
    var email: String? = null
        set(email) {
            field = this.email
        }

    var letter: String? = null
        set(letter) {
            field = this.letter
        }

    @CreatedDate
    var addTime: Date? = null
        set(addTime) {
            field = this.addTime
        }

    var baned : Boolean = false
    var su = false
    var newUser = true
    var accCode: String = ""

    @JSONField(serialize = false)
    //    @Convert(converter = ObjectConverter.class)
    //    @Column(columnDefinition = "BLOB")
    @Column(columnDefinition = JSONConverter.type)
    var publicKey: String? = null
        set(publicKey) {
            field = this.publicKey
        }

    @JSONField(serialize = false)
    //    @Convert(converter = ObjectConverter.class)
    //    @Column(columnDefinition = "BLOB")
    @Column(columnDefinition = JSONConverter.type)
    var privateKey: String? = null
        set(privateKey) {
            field = this.privateKey
        }

    @ManyToMany(mappedBy = "users")
    var quarters: List<Quarters> = ArrayList()

    @JSONField(serialize = false)
    @ManyToMany(mappedBy = "users")
    var roles: List<Role> = ArrayList()
        set(roles) {
            field = this.roles
        }

    @OneToOne(mappedBy = "user", cascade = arrayOf(CascadeType.REMOVE))
    lateinit var profile: UserProfile

    //    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    //     List<RolePermission> permissions = new ArrayList<>();

    @JSONField(serialize = false)
    @OneToMany(mappedBy = "user")
    var readList: List<MessageRead> = ArrayList()
        set(readList) {
            field = this.readList
        }


    //    @OneToMany(mappedBy = "user")
    //     List<UserExternalPermission> externalPermissions = new ArrayList<>();

    //    @JSONField(serialize = false)
    //    @OneToMany(mappedBy = "user")
    //     List<GlobalPermissionCenter> gpCenters = new ArrayList<>();


    //    @Transient
    //    public boolean hasQuarters(long id){
    //        return getQuarters().stream()
    //                    .anyMatch(q -> q.getId().equals(id));
    //    }


    //    @Transient
    //    public List<Department> getDepartments(){
    //        List<Department> departments = getQuarters().stream()
    //               .map(q -> q.getDepartment())
    //                .collect(Collectors.toList());
    //        return departments;
    //    }

    //    @Transient
    //    public List<Long> getDepartmentIds(){
    //        return getQuarters().stream()
    //                .map(q -> q.getDepartment().getId())
    //                .collect(Collectors.toList());
    //    }
    //
    //    @Transient
    //    public List<Long> getQuartersIds(){
    //        return getQuarters().stream().map(Quarters::getId).collect(Collectors.toList());
    //    }

    override fun equals(other: Any?): Boolean {
        return null != other && other is User && (other as User).id == id
    }

}


class UserAddRequeest{
    interface group1
    interface group2

    @ApiModelProperty(value = "用户名", required = true)
    @org.hibernate.validator.constraints.NotEmpty(message = "用户名不能为空", groups = arrayOf(group1::class))
    lateinit var username: String

    @ApiModelProperty(value = "密码", required = true)
    @org.hibernate.validator.constraints.NotEmpty(message = "密码不能为空", groups = arrayOf(group1::class))
    var password: String? = null

    @ApiModelProperty(value = "真实姓名", required = true)
    @org.hibernate.validator.constraints.NotEmpty(message = "真实姓名不能为空", groups = arrayOf(group1::class))
    var trueName: String? = null

    @ApiModelProperty(value = "手机号", required = true)
    @Pattern(regexp = "^1[3456789][0-9]{9}$|^.{0}$", message = "手机号码格式错误", groups = arrayOf(group1::class))
    var phone: String? = null

    @ApiModelProperty(value = "邮箱")
    @Email(groups = arrayOf(group1::class))
    var email: String? = null

    @ApiModelProperty(value = "是否禁用", required = true)
    var baned: Boolean = false

    //岗位ID列表
     var qids: List<Long> = ArrayList()
    //角色ID列表
     var rids: MutableCollection<Long> = ArrayList()

    //信贷机构代码
    var accCode: String = ""


    @AssertTrue(message = "已经有同名用户存在", groups = arrayOf(group2::class))
    fun isValidUsername(): Boolean {
        return SpringContextUtils.getBean(IUserDao::class.java).countByUsername(username) == 0
    }

    @AssertTrue(message = "已经有相同的手机号码存在", groups = arrayOf(group2::class))
    fun isValidPhone(): Boolean {
        return if (StringUtils.isEmpty(phone)) {
            true
        } else SpringContextUtils.getBean(IUserDao::class.java).countByPhone(phone) == 0
    }
}

class UserEditRequest {

    @Range(min = 1, message = "用户ID不能为空")
    var id: Long = 0

    @ApiModelProperty(value = "密码")
     var password: String? = null

    @ApiModelProperty(value = "手机号")
    @Pattern(regexp = "^1[3456789][0-9]{9}$", message = "手机号码格式错误")
     var phone: String? = null

    @ApiModelProperty(value = "真实姓名")
    var trueName: String = ""

    @ApiModelProperty(value = "邮箱")
    @Email
     var email: String? = null

    @ApiModelProperty(value = "是否禁用")
     var baned: Boolean? = null

    @ApiModelProperty(value = "用户岗位ID数组")
     var quarters: List<Long> = listOf()

    @ApiModelProperty(value = "菜单黑名单")
     var unbindMenus: Set<String>? = null

    @ApiModelProperty(value = "功能黑名单")
     var unbindMethods: Set<String>? = null

     var accCode = ""
}

class UserSearchRequest : Pager() {
    var name = ""
    var baned : Boolean? = null
    var quarters = arrayListOf<Long>()
}

