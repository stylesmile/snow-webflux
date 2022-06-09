package com.stylesmile.modules.system.repository;

import com.stylesmile.modules.system.entity.SysUser;
import com.stylesmile.modules.system.vo.query.SysUserQuery;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface SysUserRepository extends ReactiveCrudRepository<SysUser,Integer> {
    Mono<SysUser> findByUsername(String username);
    @Query("select * from sys_user")
    SysUser getSysUserByNameAndPassword(String username);

    @Query("select * from sys_user")
    List<SysUser> getUserList(SysUserQuery sysUserQuery);
    @Query("select * from sys_user")
    Boolean updateUser(SysUser user);
    @Query("select * from sys_user")
    Boolean deleteUser(Integer id);
    @Query("select * from sys_user")
    Integer queryPermission(String url, Integer userId);
}
