package com.xx.buji.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xx.buji.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
