package com.alexpower.dao;

import com.alexpower.bean.Employee;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;

public interface EmployeeMapper {

    public Employee getEmpByMap(HashMap map);

    public Employee getEmpByIdAndLastName(@Param("id") Integer id, @Param("lastName") String lastName);

    public Employee getEmpById(Integer id);

    public Integer addEmp(Employee employee);

    public Boolean updateEmp(Employee employee);

    public void deleteEmpById(Integer id);
}
