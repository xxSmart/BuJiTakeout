package com.xx.buji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xx.buji.entity.Employee;
import com.xx.buji.mapper.EmployeeMapper;
import com.xx.buji.service.EmployeeServise;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeServise {
}
