package com.snow.web.service;

import com.snow.common.biz.IService;
import com.snow.common.core.domain.entity.SysRole;
import com.snow.web.domain.SysUserRole;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

/**
 * 角色业务层
 *
 * @author ruoyi
 */
public interface ISysRoleService extends IService<SysRole, Long> {

	/**
	 * 根据用户ID查询角色列表
	 *
	 * @param userId 用户ID
	 * @return 角色列表
	 */
	public Mono<List<SysRole>> selectRolesByUserId(Long userId);

	/**
	 * 根据用户ID查询角色权限
	 *
	 * @param userId 用户ID
	 * @return 权限列表
	 */
	public Mono<Set<String>> selectRolePermissionByUserId(Long userId);

	/**
	 * 查询所有角色
	 *
	 * @return 角色列表
	 */
	public Mono<List<SysRole>> selectRoleAll();

	/**
	 * 根据用户ID获取角色选择框列表
	 *
	 * @param userId 用户ID
	 * @return 选中角色ID列表
	 */
	public Mono<List<Long>> selectRoleListByUserId(Long userId);

	/**
	 * 校验角色名称是否唯一
	 *
	 * @param role 角色信息
	 * @return 结果
	 */
	public Mono<String> checkRoleNameUnique(SysRole role);

	/**
	 * 校验角色权限是否唯一
	 *
	 * @param role 角色信息
	 * @return 结果
	 */
	public Mono<String> checkRoleKeyUnique(SysRole role);

	/**
	 * 校验角色是否允许操作
	 *
	 * @param role 角色信息
	 */
	public void checkRoleAllowed(SysRole role);

	/**
	 * 校验角色是否有数据权限
	 *
	 * @param roleId 角色id
	 * @return
	 */
	public Mono<Long> checkRoleDataScope(Long roleId);

	/**
	 * 通过角色ID查询角色使用数量
	 *
	 * @param roleId 角色ID
	 * @return 结果
	 */
	public Mono<Long> countUserRoleByRoleId(Long roleId);

	/**
	 * 修改角色状态
	 *
	 * @param role 角色信息
	 * @return 结果
	 */
	public Mono<Long> updateRoleStatus(SysRole role);

	/**
	 * 修改数据权限信息
	 *
	 * @param role 角色信息
	 * @return 结果
	 */
	public Mono<Long> authDataScope(SysRole role);

	/**
	 * 取消授权用户角色
	 *
	 * @param userRole 用户和角色关联信息
	 * @return 结果
	 */
	public Mono<Long> deleteAuthUser(SysUserRole userRole);

	/**
	 * 批量取消授权用户角色
	 *
	 * @param roleId  角色ID
	 * @param userIds 需要取消授权的用户数据ID
	 * @return 结果
	 */
	public Mono<Long> deleteAuthUsers(Long roleId, Long[] userIds);

	/**
	 * 批量选择授权用户角色
	 *
	 * @param roleId  角色ID
	 * @param userIds 需要删除的用户数据ID
	 * @return 结果
	 */
	public Mono<Long> insertAuthUsers(Long roleId, Long[] userIds);
}
