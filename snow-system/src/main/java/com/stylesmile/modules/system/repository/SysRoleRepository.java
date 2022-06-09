package com.stylesmile.modules.system.repository;

import com.stylesmile.modules.system.entity.SysRole;
import com.stylesmile.modules.system.vo.query.SysRoleQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author chenye
 * @date 2018/12/31
 */
@Repository
public interface SysRoleRepository extends ReactiveCrudRepository<SysRole, Integer> {

    /**
     * 修改角色
     *
     * @param role 角色信息
     * @return Boolean
     */
    @Query("select * from sys_user")
    Boolean updateRole(SysRole role);

    /**
     * 删除角色
     *
     * @param id 主键
     * @return Boolean
     */
    @Query("select * from sys_user")
    Boolean deleteRole(String id);

    /**
     * 检查角色编号是否重复角色
     *
     * @param code 角色编号
     * @return Boolean
     */
    @Query("select * from sys_user")
    Integer checkDuplicate(String code);


}
