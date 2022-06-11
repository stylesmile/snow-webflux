package com.snow.system.mapper;

import com.snow.system.domain.SysUserRole;
import org.apache.ibatis.annotations.Param;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 用户与角色关联表 数据层
 *
 * @author ruoyi
 */
public interface SysUserRoleMapper {
	/**
	 * 通过用户ID删除用户和角色关联
	 *
	 * @param userId 用户ID
	 * @return 结果
	 */
	public Mono<Long> deleteUserRoleByUserId(Long userId);

	/**
	 * 批量删除用户和角色关联
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public Mono<Long> deleteUserRole(Long[] ids);

	/**
	 * 通过角色ID查询角色使用数量
	 *
	 * @param roleId 角色ID
	 * @return 结果
	 */
	public Mono<Long> countUserRoleByRoleId(Long roleId);

	/**
	 * 批量新增用户角色信息
	 *
	 * @param userRoleList 用户角色列表
	 * @return 结果
	 */
	public Mono<Long> batchUserRole(List<SysUserRole> userRoleList);

	/**
	 * 删除用户和角色关联信息
	 *
	 * @param userRole 用户和角色关联信息
	 * @return 结果
	 */
	public Mono<Long> deleteUserRoleInfo(SysUserRole userRole);

	/**
	 * 批量取消授权用户角色
	 *
	 * @param roleId  角色ID
	 * @param userIds 需要删除的用户数据ID
	 * @return 结果
	 */
	public Mono<Long> deleteUserRoleInfos(@Param("roleId") Long roleId, @Param("userIds") Long[] userIds);
}