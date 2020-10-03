package com.alexpower.dao;

import com.alexpower.bean.Department;

public interface DepartmentMapper {
    public Department getDeptByIdStep(Integer did);

    public Department getDeptById(Integer did);
}
