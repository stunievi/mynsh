package com.beeasy.hzback.modules.system.request;

import com.beeasy.common.helper.SpringContextUtils;
import com.beeasy.hzback.modules.system.dao.IUserDao;
import lombok.Data;

import javax.annotation.Nullable;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserAddRequeest {
    @NotEmpty(message = "用户名不能为空")
    String username = "";

    @NotEmpty(message = "密码不能为空")
    String password = "";

    @NotEmpty(message = "真实姓名不能为空")
    String trueName = "";

    @Pattern(regexp = "^1[3456789][0-9]{9}$|^.{0}$", message = "手机号码格式错误")
    String phone = "";

    @Nullable
    @Email(message = "邮箱格式错误")
    String email;

    boolean baned = false;

    List<Long> qids = new ArrayList<>();
    List<Long> rids = new ArrayList<>();

    String accCode = "";

    @AssertTrue(message = "已经有同名用户存在")
    public boolean isValidUsername() {
        return SpringContextUtils.getBean(IUserDao.class).countByUsername(username) == 0;
    }

    @AssertTrue(message = "已经有相同的手机号码存在")
    public boolean isValidPhone() {
        if (phone.isEmpty()) {
            return true;
        } else {
            return SpringContextUtils.getBean(IUserDao.class).countByPhone(phone) == 0;
        }
    }

}
