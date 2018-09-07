package com.beeasy.hzmsback.request;

import com.beeasy.hzmsback.HzMsBackApp;
import com.beeasy.mscommon.entity.User;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserAddRequeest {
    @NotEmpty(message = "用户名不能为空")
    String username;

    @NotEmpty(message = "密码不能为空")
    String password;

    @NotEmpty(message = "真实姓名不能为空")
    String trueName;

    @Pattern(regexp = "^1[3456789][0-9]{9}$|^.{0}$", message = "手机号码格式错误")
    String phone;

    @Null
    @Email(message = "邮箱格式错误")
    String email;

    boolean baned = false;

    List<Long> qids = new ArrayList<>();
    List<Long> rids = new ArrayList<>();

    String accCode = "";

    @AssertTrue(message = "已经有同名用户存在")
    public boolean isValidUsername() {
        return HzMsBackApp.sqlManager.lambdaQuery(User.class)
                .andEq(User::getUsername, username).count() == 0;
//        User user = new User();
//        user.setUsername(username);
//        HzMsBackApp.sqlManager.templateCount(username);
//        return SpringContextUtils.getBean(IUserDao.class).countByUsername(username) == 0;
    }

    @AssertTrue(message = "已经有相同的手机号码存在")
    public boolean isValidPhone() {
        if (phone.isEmpty()) {
            return true;
        } else {
            return HzMsBackApp.sqlManager.lambdaQuery(User.class)
                    .andEq(User::getPhone, phone).count() == 0;
//            return SpringContextUtils.getBean(IUserDao.class).countByPhone(phone) == 0;
        }
    }

}
