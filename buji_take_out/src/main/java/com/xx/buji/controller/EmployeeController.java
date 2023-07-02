package com.xx.buji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xx.buji.common.Result;
import com.xx.buji.entity.Employee;
import com.xx.buji.service.EmployeeServise;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeServise employeeServise;

    /**
    * @Author: Xiangxiang_Wang
    * @Description: 员工登录
    * @DateTime: 2023/6/8 17:00
    * @Params:
    * @Return:
    **/
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1、讲页面提交的password进行md5加密处理
        String password = employee.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());
        //2、根据页面提交的username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //等值查询
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeServise.getOne(queryWrapper);
        //3、如果没有查到，返回登陆失败结果
        if (emp==null ){
            return Result.error("登陆失败");
        }else
        //4、查到了，密码比对，结果不一致，返回登陆失败
        if (!password.equals(emp.getPassword())){
           return Result.error("登陆失败");
        }else
        //5、密码一致，查看员工状态，若为禁用状态，则返回用户已禁用
        if (emp.getStatus()==0){//0为禁用，1为可用
          return  Result.error("用户已禁用，解禁请联系管理员");
        }else
        //6、未禁用，登陆成功且将员工id存入Session并返回登录结果
        {   request.getSession().setAttribute("employee",emp.getId());
            return Result.success(emp);
        }
    }
/**
* @Author: Xiangxiang_Wang
* @Description: 退出登录方法
* @DateTime: 2023/6/8 20:48
* @Params:
* @Return:
**/
@PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request){
    //清楚Session中保存的当前员工的ID
    request.getSession().removeAttribute("employee");//employee为登录时存储的，所以此时删除位置填写应为employee
        return Result.success("退出成功") ;
    }
    /**
    * @Author: Xiangxiang_Wang
    * @Description: 新增员工
    * @DateTime: 2023/6/9 17:10
    * @Params: employee
    * @Return:
    **/

    @PostMapping
    public Result<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息：{}",employee.toString());
        //设置初始密码123456,需进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        //创建人
//        employee.setCreateUser((Long) request.getSession().getAttribute("employee"));
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        employeeServise.save(employee);
        return Result.success("新增员工成功");
    }
    /**
    * @Author: Xiangxiang_Wang
    * @Description: 员工信息分页查询
    * @DateTime: 2023/6/11 11:51
    * @Params: 
    * @Return: 
    **/ 
    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize,String name){
        //构造分页构造器
        Page pageInfo= new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeServise.page(pageInfo,queryWrapper);
        return Result.success(pageInfo);
    }
    @PutMapping
    public Result<String> update(HttpServletRequest request, @RequestBody Employee employee){
        log.info(employee.toString());
        //设置修改时间和修改人
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        //更新
        employeeServise.updateById(employee);
        return Result.success("员工信息修改成功！");
    }

    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工id");
        Employee employee = employeeServise.getById(id);
        if (employee!=null){
            return Result.success(employee);
        }else return Result.error("没有查询到员工信息");
    }
}
