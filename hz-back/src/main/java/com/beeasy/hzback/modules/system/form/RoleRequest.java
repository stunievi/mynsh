package com.beeasy.hzback.modules.system.form;

import com.beeasy.hzback.core.helper.SpringContextUtils;
import com.beeasy.hzback.modules.system.dao.IRoleDao;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

@Data
public class RoleRequest {
    public interface add{}
    public interface edit{}

    @NotNull(groups = edit.class)
    Long id;

    @NotEmpty(groups = {edit.class,add.class})
    String name;

    String info;

    @AssertTrue(message = "已经有同名的角色", groups = {add.class,edit.class})
    private boolean addHasName(){
        IRoleDao roleDao = SpringContextUtils.getBean(IRoleDao.class);
        return roleDao.countByNameAndIdNot(name,id) == 0;
    }

    @Range(min = 0, max = 255, message = "排序在0-255之间", groups = {add.class,edit.class})
    int sort;

}
