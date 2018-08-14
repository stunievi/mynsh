package com.beeasy.hzback.modules.system.request;

import com.beeasy.common.helper.SpringContextUtils;
import com.beeasy.hzback.modules.system.dao.IDepartmentDao;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class QuartersAddRequest {
    @NotEmpty(message = "岗位名称不能为空")
    String name = "";

    @Range(min = 1, message = "所属部门填写错误")
    long departmentId = 0;

    String info = "";

    @Range(min = 0, max = 255, message = "排序在0-255之间")
    short sort = 0;

    boolean manager = false;

    @AssertTrue(message = "已经有同名的岗位")
    public boolean isValidName() {
        return SpringContextUtils.getBean(IDepartmentDao.class).countByParentIdAndName(departmentId, name) == 0;
    }
}
