package com.beeasy.hzback.modules.system.request;

import com.beeasy.common.entity.Department;
import com.beeasy.common.helper.SpringContextUtils;
import com.beeasy.hzback.modules.system.dao.IDepartmentDao;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.AssertTrue;
import java.util.Objects;

@Getter
@Setter
public class DepartmentEditRequest {
    @Range(min = 1, message = "部门ID不能为空")
    long id = 0;

    String name = "";

    String info = "";

    @Range(min = 0, max = 255, message = "排序在0-255之间")
    short sort = 0;

    String accCode = "";

    @AssertTrue(message = "已经存在同名部门")
    public boolean isValidName() {
        IDepartmentDao departmentDao = SpringContextUtils.getBean(IDepartmentDao.class);
        Department department = departmentDao.findById(id).orElse(null);
//        Objects.requireNonNull(department, String.format("找不到id为%d的部门", id));
        if (null == department) {
            return false;
        }
        return departmentDao.countByParentIdAndNameAndIdNot(department.getParentId(), name, department.getId()) == 0;
    }

}
