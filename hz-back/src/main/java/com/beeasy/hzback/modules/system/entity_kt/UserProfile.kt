package com.beeasy.hzback.modules.system.entity_kt

import com.alibaba.fastjson.annotation.JSONField
import com.beeasy.hzback.core.entity.AbstractBaseEntity
import lombok.extern.slf4j.Slf4j
import org.hibernate.validator.constraints.Email
import javax.persistence.*
import javax.validation.constraints.Pattern


@Slf4j
@Entity
@Table(name = "t_user_profile")
class UserProfile : AbstractBaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @JSONField(serialize = false)
    @OneToOne
    lateinit var user: User

    var faceId: Long? = null
        set(faceId) {
            field = this.faceId
        }

    @JSONField(serialize = false)
    var cloudUsername: String = ""

    @JSONField(serialize = false)
    var cloudPassword: String = ""

}

class ProfileEditRequest {
    //    @NotNull(message = "用户ID不能为空")
    //    Long id;

    @Pattern(regexp = "^[\\u4E00-\\u9FA5\\uf900-\\ufa2d·s]{2,20}$", message = "请输入正确的姓名")
     var trueName: String = ""

    @Pattern(regexp = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$", message = "请输入正确的手机号码")
     var phone: String = ""

    @Email(message = "请输入正确的邮箱")
     var email: String = ""
}