package com.stylesmile.modules.system.repository;

import com.stylesmile.modules.system.entity.SysRoleMenu;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户mapper
 *
 * @author chenye
 * @date 2019/01/20
 */
@Repository
public interface SysRoleMenuMapper extends ReactiveCrudRepository<SysRoleMenu, Integer> {
    /**
     * 通过用户id 查询该用户拥有的菜单
     *
     * @param roleId 角色id
     * @return List<SysRoleMenu>
     */
    @Query("select * from sys_user")
    List<Integer> getRoleMenuList(Integer roleId);
}
