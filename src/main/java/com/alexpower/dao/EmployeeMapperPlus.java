package com.alexpower.dao;

import com.alexpower.bean.Employee;

import java.util.Map;

public interface EmployeeMapperPlus {

    public Employee getEmpByIdSteps(Integer id);

    public Employee getEmpWithDeptById(Integer id);

    public Employee getEmpById(Integer id);
}
