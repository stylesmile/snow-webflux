package com.snow.system.service;

import java.util.List;
import java.util.Set;

import com.snow.common.biz.IService;
import com.snow.common.core.domain.TreeSelect;
import com.snow.common.core.domain.entity.SysMenu;
import com.snow.system.domain.vo.RouterVo;
import reactor.core.publisher.Mono;

/**
 * 菜单 业务层
 * 
 * @author ruoyi
 */
public interface ISysMenuService extends IService<SysMenu, Long>
{
    /**
     * 根据用户查询系统菜单列表
     * 
     * @param userId 用户ID
     * @return 菜单列表
     */
    public Mono<List<SysMenu>> selectMenuList(Long userId);

    /**
     * 根据用户查询系统菜单列表
     * 
     * @param menu 菜单信息
     * @param userId 用户ID
     * @return 菜单列表
     */
    public Mono<List<SysMenu>> selectMenuList(SysMenu menu, Long userId);

    /**
     * 根据用户ID查询权限
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    public Mono<Set<String>> selectMenuPermsByUserId(Long userId);

    /**
     * 根据用户ID查询菜单树信息
     * 
     * @param userId 用户ID
     * @return 菜单列表
     */
    public Mono<List<SysMenu>> selectMenuTreeByUserId(Long userId);

    /**
     * 根据角色ID查询菜单树信息
     * 
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    public Mono<List<Long>> selectMenuListByRoleId(Long roleId);

    /**
     * 构建前端路由所需要的菜单
     * 
     * @param menus 菜单列表
     * @return 路由列表
     */
    public List<RouterVo> buildMenus(List<SysMenu> menus);

    /**
     * 构建前端所需要树结构
     * 
     * @param menus 菜单列表
     * @return 树结构列表
     */
    public List<SysMenu> buildMenuTree(List<SysMenu> menus);

    /**
     * 构建前端所需要下拉树结构
     * 
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus);



    /**
     * 是否存在菜单子节点
     * 
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    public Mono<Boolean> hasChildByMenuId(Long menuId);

    /**
     * 查询菜单是否存在角色
     * 
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    public Mono<Boolean> checkMenuExistRole(Long menuId);



    /**
     * 校验菜单名称是否唯一
     * 
     * @param menu 菜单信息
     * @return 结果
     */
    public Mono<String> checkMenuNameUnique(SysMenu menu);
}
