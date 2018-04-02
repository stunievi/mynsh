package com.beeasy.hzback.modules.system.service;

import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.modules.setting.entity.Department;
import com.beeasy.hzback.modules.system.form.DepartmentAdd;

public interface IDepartmentService {

    Department createDepartment(DepartmentAdd add) throws RestException;

    void deleteDepartment(long id);
}
