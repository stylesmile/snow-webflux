package com.snow.services;

import com.snow.models.Employee;
import com.snow.tool.GsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;

@SpringBootTest
class EmployeeServiceTest {
    @Resource
    EmployeeService employeeService;

    @Test
    public void list() {
        Flux<Employee> employeeFlux = employeeService.findAll();
        String json = GsonUtil.objectToJson(employeeFlux);
        System.out.println(json);
    }
}