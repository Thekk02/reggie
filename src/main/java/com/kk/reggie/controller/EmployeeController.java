package com.kk.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kk.reggie.common.R;
import com.kk.reggie.entity.Employee;
import com.kk.reggie.service.EmployeeService;
import com.kk.reggie.service.impl.EmployeeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;


/**
 * @author kk
 * @description 用户控制层
 * @date 2023-08-26 10:16:41
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeServiceImpl employeeService;

    /**
    * @Description:员工登录
    * @Params: employee
    * @Return:
    */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1.将页面提交的密码password及逆行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3.如果没有拆线呢到则返回登录失败结果
        if(emp == null){
            return R.error("登陆失败");
        }

        //4.密码对比，如果不一致则返回登陆失败结果
        if(!emp.getPassword().equals(password)){
            return R.error("登陆失败");
        }

        //5.查看员工的状态，如果为已禁用的状态，返回用户不能用
        if(emp.getStatus() == 0){
            return R.error("员工已禁用");
        }

        //6.登陆成功，将员工id存入session并返回登陆成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
    * @Description:员工登出
    * @Params:
    * @Return:
    */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理Session中保存的的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
    * @Description:新增员工
    * @Params:
    * @Return:
    */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息：{}",employee.toString());

        //设置初始密码为123456，需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex(("123456".getBytes())));

        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //获取当前登录用户的id
        Long empId = (Long) request.getSession().getAttribute("employee");

        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);

        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);

        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();

        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }



}
