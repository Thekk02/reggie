package com.kk.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kk.reggie.entity.Employee;
import com.kk.reggie.mapper.EmployeeMapper;
import com.kk.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author kk
 * @description 用户服务实现类
 * @date 2023-08-26 10:14:41
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService {

}
