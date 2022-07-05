package com.snow.web.mapper;

import com.snow.common.biz.IMapper;
import org.apache.ibatis.annotations.Param;
import com.snow.common.core.domain.entity.SysMenu;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 菜单表 数据层
 *
 * @author ruoyi
 */
public interface SysMenuMapper extends IMapper<SysMenu, Long> {

	/**
	 * 根据主键ID查询
	 *
	 * @param id 主键ID
	 * @return 返回数据对象
	 */
	@Override
	public Mono<SysMenu> selectById(Long id);

	/**
	 * 分页查询
	 *
	 * @param where 条件
	 * @return 查询结果
	 */
	@Override
	public Flux<SysMenu> selectList(SysMenu where);


	/**
	 * 根据用户所有权限
	 *
	 * @return 权限列表
	 */
	public Flux<String> selectMenuPerms();

	/**
	 * 根据用户查询系统菜单列表
	 *
	 * @param menu 菜单信息
	 * @return 菜单列表
	 */
	public Flux<SysMenu> selectMenuListByUserId(SysMenu menu);

	/**
	 * 根据用户ID查询权限
	 *
	 * @param userId 用户ID
	 * @return 权限列表
	 */
	public Flux<String> selectMenuPermsByUserId(Long userId);

	/**
	 * 根据用户ID查询菜单
	 *
	 * @return 菜单列表
	 */
	public Flux<SysMenu> selectMenuTreeAll();

	/**
	 * 根据用户ID查询菜单
	 *
	 * @param userId 用户ID
	 * @return 菜单列表
	 */
	public Flux<SysMenu> selectMenuTreeByUserId(Long userId);

	/**
	 * 根据角色ID查询菜单树信息
	 *
	 * @param roleId            角色ID
	 * @param menuCheckStrictly 菜单树选择项是否关联显示
	 * @return 选中菜单列表
	 */
	public Flux<Long> selectMenuListByRoleId(@Param("roleId") Long roleId, @Param("menuCheckStrictly") boolean menuCheckStrictly);


	/**
	 * 是否存在菜单子节点
	 *
	 * @param menuId 菜单ID
	 * @return 结果
	 */
	public Mono<Long> hasChildByMenuId(Long menuId);

	/**
	 * 校验菜单名称是否唯一
	 *
	 * @param menuName 菜单名称
	 * @param parentId 父菜单ID
	 * @return 结果
	 */
	public Mono<SysMenu> checkMenuNameUnique(@Param("menuName") String menuName, @Param("parentId") Long parentId);
}
