package com.beeasy.hzback.modules.system.request;

import com.beeasy.common.helper.SpringContextUtils;
import com.beeasy.hzback.modules.system.dao.IQuartersDao;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;

@Data
public class QuartersEditRequest {
    @Range(min = 0, message = "岗位ID填写错误")
    long id = 0;

    @NotEmpty(message = "岗位名不能为空")
    String name = "";

    String info = "";
    boolean manager = false;

    @Range(min = 0, max = 255, message = "排序在0-255之间")
    short sort = 0;

    @AssertTrue(message = "所属部门下已经有同名岗位")
    public boolean isValidName() {
        return SpringContextUtils.getBean(IQuartersDao.class).countSameNameFromDepartment(id, name) == 0;
    }
}
