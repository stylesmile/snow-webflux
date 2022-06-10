package com.snow.system.service;

import com.snow.common.biz.IService;
import com.snow.common.core.domain.TreeSelect;
import com.snow.common.core.domain.entity.SysDept;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 部门管理 服务层
 *
 * @author ruoyi
 */
public interface ISysDeptService extends IService<SysDept, Long> {

	/**
	 * 构建前端所需要树结构
	 *
	 * @param depts 部门列表
	 * @return 树结构列表
	 */
	public List<SysDept> buildDeptTree(List<SysDept> depts);

	/**
	 * 构建前端所需要下拉树结构
	 *
	 * @param depts 部门列表
	 * @return 下拉树结构列表
	 */
	public List<TreeSelect> buildDeptTreeSelect(List<SysDept> depts);

	/**
	 * 根据角色ID查询部门树信息
	 *
	 * @param roleId 角色ID
	 * @return 选中部门列表
	 */
	public Mono<List<Long>> selectDeptListByRoleId(Long roleId);

	/**
	 * 根据ID查询所有子部门（正常状态）
	 *
	 * @param deptId 部门ID
	 * @return 子部门数
	 */
	public Mono<Long> selectNormalChildrenDeptById(Long deptId);

	/**
	 * 是否存在部门子节点
	 *
	 * @param deptId 部门ID
	 * @return 结果
	 */
	public Mono<Boolean> hasChildByDeptId(Long deptId);

	/**
	 * 查询部门是否存在用户
	 *
	 * @param deptId 部门ID
	 * @return 结果 true 存在 false 不存在
	 */
	public Mono<Boolean> checkDeptExistUser(Long deptId);

	/**
	 * 校验部门名称是否唯一
	 *
	 * @param dept 部门信息
	 * @return 结果
	 */
	public Mono<String> checkDeptNameUnique(SysDept dept);

	/**
	 * 校验部门是否有数据权限
	 *
	 * @param deptId 部门id
	 * @return
	 */
	public Mono<Long> checkDeptDataScope(Long deptId);
}
