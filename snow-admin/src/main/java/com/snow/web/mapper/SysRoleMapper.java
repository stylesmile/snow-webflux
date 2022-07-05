package com.snow.web.mapper;

import com.snow.common.biz.IMapper;
import com.snow.common.core.domain.entity.SysRole;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 角色表 数据层
 *
 * @author ruoyi
 */
public interface SysRoleMapper extends IMapper<SysRole, Long> {

	/**
	 * 根据主键ID查询
	 *
	 * @param id 主键ID
	 * @return 返回数据对象
	 */
	@Override
	public Mono<SysRole> selectById(Long id);

	/**
	 * 分页查询
	 *
	 * @param where 条件
	 * @return 查询结果
	 */
	@Override
	public Flux<SysRole> selectList(SysRole where);

	/**
	 * 根据用户ID查询角色
	 *
	 * @param userId 用户ID
	 * @return 角色列表
	 */
	public Flux<SysRole> selectRolePermissionByUserId(Long userId);

	/**
	 * 查询所有角色
	 *
	 * @return 角色列表
	 */
	public Flux<SysRole> selectRoleAll();

	/**
	 * 根据用户ID获取角色选择框列表
	 *
	 * @param userId 用户ID
	 * @return 选中角色ID列表
	 */
	public Flux<Long> selectRoleListByUserId(Long userId);

	/**
	 * 根据用户ID查询角色
	 *
	 * @param userName 用户名
	 * @return 角色列表
	 */
	public Flux<SysRole> selectRolesByUserName(String userName);

	/**
	 * 校验角色名称是否唯一
	 *
	 * @param roleName 角色名称
	 * @return 角色信息
	 */
	public Mono<SysRole> checkRoleNameUnique(String roleName);

	/**
	 * 校验角色权限是否唯一
	 *
	 * @param roleKey 角色权限
	 * @return 角色信息
	 */
	public Mono<SysRole> checkRoleKeyUnique(String roleKey);

}
