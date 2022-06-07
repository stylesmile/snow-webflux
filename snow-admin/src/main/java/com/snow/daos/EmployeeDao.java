package com.snow.daos;

import com.snow.models.Employee;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author: ffzs
 * @Date: 2020/8/10 下午4:27
 */


public interface EmployeeDao extends ReactiveCrudRepository<Employee, Long> {

    Flux<Employee> findByName(String name);

}
