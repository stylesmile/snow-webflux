package com.snow.services;

import com.snow.daos.EmployeeDao;
import com.snow.models.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author: ffzs
 * @Date: 2020/8/10 下午4:32
 */

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeDao employeeDao;

    public Flux<Employee> findAll () {
        return employeeDao.findAll();
    }

    public Mono<Employee> findById (long id) {
        return employeeDao.findById(id);
    }

    public Flux<Employee> findByName (String name) {
        return employeeDao.findByName(name);
    }

    public Mono<Employee> save (Employee employee) {
        return employeeDao.save(employee);
    }

    public Mono<Void> update (Employee employee) {
        return findById(employee.getId())
                .map(em -> employee.withId(em.getId()))
                .flatMap(employeeDao::save)
                .then();
    }

    public Mono<Void> delete (Employee employee) {
        return employeeDao.delete(employee);
    }

    public Mono<Void> deleteById (long id) {
        return employeeDao.deleteById(id);
    }
}
