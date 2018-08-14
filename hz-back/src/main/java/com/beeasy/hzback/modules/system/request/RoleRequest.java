package com.beeasy.hzback.modules.system.request;

import com.beeasy.common.helper.SpringContextUtils;
import com.beeasy.hzback.modules.system.dao.IRoleDao;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;

@Data
public class RoleRequest {

    public interface Edit {
    }

    public interface Add {
    }

    @Range(min = 1, groups = {Edit.class}, message = "角色ID填写错误")
    long id = 0;

    @NotEmpty(groups = {Add.class, Edit.class}, message = "角色名不能为空")
    String name = "";

    String info = "";

    boolean canDelete = false;

    @Range(min = 0, max = 255, message = "排序在0-255之间")
    short sort = 0;

    @AssertTrue(groups = {Add.class, Edit.class}, message = "已经有同名的角色")
    public boolean isValidName() {
        return SpringContextUtils.getBean(IRoleDao.class).countByNameAndIdNot(name, id) == 0;
    }

}
