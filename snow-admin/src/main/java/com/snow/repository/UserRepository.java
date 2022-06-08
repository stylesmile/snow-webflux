package com.snow.repository;

import com.snow.entity.SysUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<SysUser,Integer> {
    Mono<SysUser> findByUsername(String username);

}
