package com.beeasy.hzback.modules.system.form;

import com.beeasy.hzback.core.helper.SpringContextUtils;
import com.beeasy.hzback.modules.system.dao.IUserDao;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.GroupSequence;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@ApiModel
@Data
@GroupSequence({UserAdd.group1.class, UserAdd.group2.class, UserAdd.class})
public class UserAdd {

    public interface group1 {}
    public interface group2 {}

    @ApiModelProperty(value = "用户名",required = true)
    @NotEmpty(message = "用户名不能为空", groups = {group1.class})
    private String username;

    @ApiModelProperty(value = "密码",required = true)
    @NotEmpty(message = "密码不能为空", groups = {group1.class})
    private String password;

    @ApiModelProperty(value = "真实姓名",required = true)
    @NotEmpty(message = "真实姓名不能为空",groups = {group1.class})
    private String trueName;

    @ApiModelProperty(value = "手机号",required = true)
    @Pattern(regexp = "^1[3456789][0-9]{9}$|^.{0}$",message = "手机号码格式错误", groups = group1.class)
    private String phone;

    @ApiModelProperty(value = "邮箱")
    @Email(groups = group1.class)
    private String email;

    @ApiModelProperty(value = "是否禁用", required = true)
    private boolean baned;

    //岗位ID列表
    List<Long> qids = new ArrayList<>();
    //角色ID列表
    List<Long> rids = new ArrayList<>();

    //信贷机构代码
    String accCode;


    @AssertTrue(message = "已经有同名用户存在", groups = group2.class)
    public boolean isValidUsername(){
        return SpringContextUtils.getBean(IUserDao.class).countByUsername(username) == 0;
    }

    @AssertTrue(message = "已经有相同的手机号码存在",groups = group2.class)
    public boolean isValidPhone(){
        if(StringUtils.isEmpty(phone)){
            return true;
        }
        return SpringContextUtils.getBean(IUserDao.class).countByPhone(phone) == 0;
    }




}
