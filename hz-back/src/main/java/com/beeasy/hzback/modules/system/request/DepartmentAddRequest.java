package com.beeasy.hzback.modules.system.request;

import com.beeasy.common.helper.SpringContextUtils;
import com.beeasy.hzback.modules.system.dao.IDepartmentDao;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class DepartmentAddRequest {
    @NotEmpty(message = "部门名不能为空")
    String name = "";

    long parentId = 0;

    String info = "";

    short sort = 0;

    String accCode = "";

    @AssertTrue(message = "已经有同名的部门")
    public boolean isValidDepartment() {
        if (0 == parentId) {
            return SpringContextUtils.getBean(IDepartmentDao.class).countByParentAndName(null, name) == 0;
        } else {
            return SpringContextUtils.getBean(IDepartmentDao.class).countByParentIdAndName(parentId, name) == 0;
        }
    }
}
