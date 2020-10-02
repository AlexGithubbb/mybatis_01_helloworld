package com.alexpower.dao;

import com.alexpower.bean.Employee;
import jdk.nashorn.internal.ir.EmptyNode;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

public interface EmployeeMapper {

    public List<Employee> getEmpsByNameLike(String name);

    public Employee getEmpByMap(HashMap map);

    public Employee getEmpByIdAndLastName(@Param("id") Integer id, @Param("lastName") String lastName);

    public Employee getEmpById(Integer id);

    public Integer addEmp(Employee employee);

    public Boolean updateEmp(Employee employee);

    public void deleteEmpById(Integer id);
}
