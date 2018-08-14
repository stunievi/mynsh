package com.beeasy.hzback.modules.system.request;

import com.beeasy.common.helper.SpringContextUtils;
import com.beeasy.hzback.modules.system.dao.IUserDao;
import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserEditRequest {
    long id = 0;
    String password = "";

    @Pattern(regexp = "^1[3456789][0-9]{9}$", message = "手机号码格式错误")
    String phone = "";
    String trueName = "";

    @Email
    String email = "";
    Boolean baned = null;

    List<Long> quarters;
    String accCode = "";

    @AssertTrue(message = "已经有相同的手机号码")
    public boolean isValidPhone() {
        if (phone.isEmpty()) {
            return true;
        }
        return SpringContextUtils.getBean(IUserDao.class).countByPhoneAndIdNot(phone, id) == 0;
    }
}
