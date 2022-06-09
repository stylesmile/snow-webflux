package com.stylesmile.modules.system.repository;

import com.stylesmile.modules.system.entity.SysRole;
import com.stylesmile.modules.system.entity.SysUserRole;
import com.stylesmile.modules.system.vo.query.SysRoleQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 用户mapper
 *
 * @author chenye
 * @date 2019/01/20
 */
@Repository
public interface SysUserRoleRepository extends ReactiveCrudRepository<SysRole, Integer> {
    @Query("select * from sys_user")
    void saveAll(List<SysUserRole> sysUserRoleList);
}
