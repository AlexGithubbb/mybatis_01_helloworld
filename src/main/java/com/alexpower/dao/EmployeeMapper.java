package com.alexpower.dao;

import com.alexpower.bean.Employee;
import jdk.nashorn.internal.ir.EmptyNode;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface EmployeeMapper {
    /* return multiple records as map
    * @MapKey("id") will tell mybatis use "id" as the key of map
    * */
    @MapKey("id")
    public Map<Integer, Employee> getEmpsByNameLikeReturnMap(String name);

    // return single record as map
    public Map<String, Object> getEmpByIdReturnMap(Integer id);

    public List<Employee> getEmpsByNameLike(String name);

    public Employee getEmpByMap(HashMap map);

    public Employee getEmpByIdAndLastName(@Param("id") Integer id, @Param("lastName") String lastName);

    public Employee getEmpById(Integer id);

    public Integer addEmp(Employee employee);

    public Boolean updateEmp(Employee employee);

    public void deleteEmpById(Integer id);
}
