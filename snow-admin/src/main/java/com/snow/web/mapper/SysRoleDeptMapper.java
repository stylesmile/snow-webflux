package com.snow.web.mapper;

import com.snow.web.domain.SysRoleDept;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 角色与部门关联表 数据层
 *
 * @author ruoyi
 */
public interface SysRoleDeptMapper {

	/**
	 * 通过角色ID删除角色和部门关联
	 *
	 * @param roleId 角色ID
	 * @return 结果
	 */
	public Mono<Long> deleteRoleDeptByRoleId(Long roleId);

	/**
	 * 批量删除角色部门关联信息
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public Mono<Long> deleteRoleDept(Long[] ids);

	/**
	 * 查询部门使用数量
	 *
	 * @param deptId 部门ID
	 * @return 结果
	 */
	public Mono<Long> selectCountRoleDeptByDeptId(Long deptId);

	/**
	 * 批量新增角色部门信息
	 *
	 * @param roleDeptList 角色部门列表
	 * @return 结果
	 */
	public Mono<Long> batchRoleDept(List<SysRoleDept> roleDeptList);
}
