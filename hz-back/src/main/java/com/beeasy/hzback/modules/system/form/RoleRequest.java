package com.beeasy.hzback.modules.system.form;

import com.beeasy.hzback.core.helper.SpringContextUtils;
import com.beeasy.hzback.modules.system.dao.IRoleDao;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

@Data
public class RoleRequest {
    public interface add{}
    public interface edit{}

    @NotNull(groups = edit.class)
    Long id;

    @NotEmpty
    String name;

    String info;

    @AssertTrue(message = "已经有同名的角色")
    private boolean addHasName(){
        IRoleDao roleDao = SpringContextUtils.getBean(IRoleDao.class);
        return roleDao.countByNameAndIdNot(name,id) == 0;
    }


}
