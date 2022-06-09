package com.stylesmile.modules.system.service;

import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 用户管理
 *
 * @author chenye
 * @date 2019/1/8
 */
public interface SysRoleMenuService  {

    /**
     * 角色增加菜单
     *
     * @param roleId  角色id
     * @param menuIds 以逗号分隔的菜单id
     * @return Boolean
     */
    Boolean addRoleMenu(Integer roleId, String menuIds);

    /**
     * 通过角色id 查询该用户拥有的菜单
     *
     * @param roleId 角色id
     * @return List<SysRoleMenu>
     */
    List<Integer> getRoleMenuList(Integer roleId);

    Mono<Void> removeByIds(List<Integer> strToLongList);
}
