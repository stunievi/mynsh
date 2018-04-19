package com.beeasy.hzback.modules.system.service;

import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.modules.system.entity.Department;
import com.beeasy.hzback.modules.system.form.DepartmentAdd;

public interface IDepartmentService {


    Result<Department> createDepartment(DepartmentAdd add) throws RestException;

    Result deleteDepartment(long id);
}
