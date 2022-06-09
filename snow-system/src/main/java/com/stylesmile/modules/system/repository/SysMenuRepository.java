package com.stylesmile.modules.system.repository;

import com.stylesmile.modules.system.entity.SysMenu;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chenye
 * @date 2018/11/18
 */
@Repository
public interface SysMenuRepository extends ReactiveCrudRepository<SysMenu, Integer> {
    /**
     * 查询全部菜单
     *
     * @return list
     */
    @Query("select * from sys_user")
    List<SysMenu> getMenuList();

    /**
     * 通过用户id获取当前用户的菜单
     *
     * @param userId 用户id
     * @return List<SysMenu>
     */
    @Query("select * from sys_user")
    List<SysMenu> getMenuListByUserId(Integer userId);

    /**
     * 更新菜单
     *
     * @param sysMenu 菜单
     * @return Boolean
     */
    @Query("select * from sys_user")
    Boolean updateMenu(SysMenu sysMenu);

    /**
     * 删除
     *
     * @param id 主键
     * @return Boolean
     */
    @Query("select * from sys_user")
    Boolean deleteMenu(String id);

}
