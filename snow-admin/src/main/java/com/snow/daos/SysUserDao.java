package com.snow.daos;

import com.snow.entity.SysUser;
import com.snow.models.Employee;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author: chenye
 * @Date: 2022/4/10 下午4:27
 */


public interface SysUserDao extends ReactiveCrudRepository<SysUser, Integer> {

    Mono<SysUser> findByName(String name);

}
