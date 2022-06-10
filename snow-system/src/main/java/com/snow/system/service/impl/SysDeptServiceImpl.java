package com.snow.system.service.impl;

import com.snow.common.annotation.DataScope;
import com.snow.common.biz.BaseServiceImpl;
import com.snow.common.constant.UserConstants;
import com.snow.common.core.domain.TreeSelect;
import com.snow.common.core.domain.entity.SysDept;
import com.snow.common.core.domain.entity.SysUser;
import com.snow.common.core.text.Convert;
import com.snow.common.exception.ServiceException;
import com.snow.common.utils.SecurityUtils;
import com.snow.common.utils.StringUtils;
import com.snow.common.utils.spring.SpringUtils;
import com.snow.system.mapper.SysDeptMapper;
import com.snow.system.mapper.SysRoleMapper;
import com.snow.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门管理 服务实现
 *
 * @author ruoyi
 */
@Service
public class SysDeptServiceImpl
	extends BaseServiceImpl<SysDept, Long, SysDeptMapper>
	implements ISysDeptService {

	@Autowired
	private SysRoleMapper roleMapper;

	/**
	 * 查询部门管理数据
	 *
	 * @param dept 部门信息
	 * @return 部门信息集合
	 */
	@Override
	@DataScope(deptAlias = "d")
	public Mono<List<SysDept>> selectList(SysDept dept) {
		return super.selectList(dept);
	}

	/**
	 * 构建前端所需要树结构
	 *
	 * @param depts 部门列表
	 * @return 树结构列表
	 */
	@Override
	public List<SysDept> buildDeptTree(List<SysDept> depts) {
		List<SysDept> returnList = new ArrayList<>();
		List<Long> tempList = new ArrayList<>();
		for (SysDept dept : depts) {
			tempList.add(dept.getDeptId());
		}
		for (SysDept dept : depts) {
			// 如果是顶级节点, 遍历该父节点的所有子节点
			if (!tempList.contains(dept.getParentId())) {
				recursionFn(depts, dept);
				returnList.add(dept);
			}
		}
		if (returnList.isEmpty()) {
			returnList = depts;
		}
		return returnList;
	}

	/**
	 * 构建前端所需要下拉树结构
	 *
	 * @param depts 部门列表
	 * @return 下拉树结构列表
	 */
	@Override
	public List<TreeSelect> buildDeptTreeSelect(List<SysDept> depts) {
		List<SysDept> deptTrees = buildDeptTree(depts);
		return deptTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
	}

	/**
	 * 根据角色ID查询部门树信息
	 *
	 * @param roleId 角色ID
	 * @return 选中部门列表
	 */
	@Override
	public Mono<List<Long>> selectDeptListByRoleId(Long roleId) {
		return roleMapper.selectById(roleId).flatMap(role -> {
			return mapper.selectDeptListByRoleId(roleId, role.isDeptCheckStrictly()).collectList();
		});
	}

	/**
	 * 根据ID查询所有子部门（正常状态）
	 *
	 * @param deptId 部门ID
	 * @return 子部门数
	 */
	@Override
	public Mono<Long> selectNormalChildrenDeptById(Long deptId) {
		return mapper.selectNormalChildrenDeptById(deptId);
	}

	/**
	 * 是否存在子节点
	 *
	 * @param deptId 部门ID
	 * @return 结果
	 */
	@Override
	public Mono<Boolean> hasChildByDeptId(Long deptId) {
		return mapper.hasChildByDeptId(deptId).map(result -> {
			return result > 0;
		});

	}

	/**
	 * 查询部门是否存在用户
	 *
	 * @param deptId 部门ID
	 * @return 结果 true 存在 false 不存在
	 */
	@Override
	public Mono<Boolean> checkDeptExistUser(Long deptId) {
		return mapper.checkDeptExistUser(deptId).map(result -> {
			return result > 0;
		});

	}

	/**
	 * 校验部门名称是否唯一
	 *
	 * @param dept 部门信息
	 * @return 结果
	 */
	@Override
	public Mono<String> checkDeptNameUnique(SysDept dept) {
		Long deptId = StringUtils.isNull(dept.getDeptId()) ? -1L : dept.getDeptId();
		return mapper.checkDeptNameUnique(dept.getDeptName(), dept.getParentId()).map(info -> {
			if (StringUtils.isNotNull(info) && info.getDeptId().longValue() != deptId.longValue()) {
				return UserConstants.NOT_UNIQUE;
			}
			return UserConstants.UNIQUE;
		});
	}

	/**
	 * 校验部门是否有数据权限
	 *
	 * @param deptId 部门id
	 * @return
	 */
	@Override
	public Mono<Long> checkDeptDataScope(Long deptId) {
		return SecurityUtils.getUserId().flatMap(loginUserId -> {
			if (!SysUser.isAdmin(loginUserId)) {
				SysDept dept = new SysDept();
				dept.setDeptId(deptId);
				return SpringUtils.getAopProxy(this).selectList(dept).doOnSuccess(depts -> {
					if (StringUtils.isEmpty(depts)) {
						throw new ServiceException("没有权限访问部门数据！");
					}
				}).thenReturn(1L);
			}
			return Mono.just(1L);
		});
	}

	/**
	 * 新增保存部门信息
	 *
	 * @param dept 部门信息
	 * @return 结果
	 */
	@Override
	public Mono<Long> insert(SysDept dept) {
		return mapper.selectById(dept.getParentId()).flatMap(info -> {
			// 如果父节点不为正常状态,则不允许新增子节点
			if (!UserConstants.DEPT_NORMAL.equals(info.getStatus())) {
				throw new ServiceException("部门停用，不允许新增");
			}
			dept.setAncestors(info.getAncestors() + "," + dept.getParentId());
			return mapper.insert(dept);
		});
	}

	/**
	 * 修改保存部门信息
	 *
	 * @param dept 部门信息
	 * @return 结果
	 */
	@Override
	public Mono<Long> update(SysDept dept) {
		return mapper.selectById(dept.getParentId()).flatMap(newParentDept -> {
			return mapper.selectById(dept.getDeptId()).flatMap(oldDept -> {
				if (StringUtils.isNotNull(newParentDept) && StringUtils.isNotNull(oldDept)) {
					String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getDeptId();
					String oldAncestors = oldDept.getAncestors();
					dept.setAncestors(newAncestors);
					return updateDeptChildren(dept.getDeptId(), newAncestors, oldAncestors);
				}
				return Mono.just(0);
			}).flatMap(o -> {
				return mapper.update(dept).flatMap(result -> {
					if (UserConstants.DEPT_NORMAL.equals(dept.getStatus()) && StringUtils.isNotEmpty(dept.getAncestors())
						&& !StringUtils.equals("0", dept.getAncestors())) {
						// 如果该部门是启用状态，则启用该部门的所有上级部门
						return updateParentDeptStatusNormal(dept).thenReturn(result);
					}
					return Mono.just(result);
				});
			});
		});
	}

	/**
	 * 修改该部门的父级部门状态
	 *
	 * @param dept 当前部门
	 */
	private Mono<Long> updateParentDeptStatusNormal(SysDept dept) {
		String ancestors = dept.getAncestors();
		Long[] deptIds = Convert.toLongArray(ancestors);
		return mapper.updateDeptStatusNormal(deptIds);
	}

	/**
	 * 修改子元素关系
	 *
	 * @param deptId       被修改的部门ID
	 * @param newAncestors 新的父ID集合
	 * @param oldAncestors 旧的父ID集合
	 */
	public Mono<Long> updateDeptChildren(Long deptId, String newAncestors, String oldAncestors) {
		return mapper.selectChildrenDeptById(deptId).collectList().flatMap(children -> {
			for (SysDept child : children) {
				child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
			}
			if (children.size() > 0) {
				return mapper.updateDeptChildren(children);
			}
			return Mono.just(1L);
		});
	}

	/**
	 * 递归列表
	 */
	private void recursionFn(List<SysDept> list, SysDept t) {
		// 得到子节点列表
		List<SysDept> childList = getChildList(list, t);
		t.setChildren(childList);
		for (SysDept tChild : childList) {
			if (hasChild(list, tChild)) {
				recursionFn(list, tChild);
			}
		}
	}

	/**
	 * 得到子节点列表
	 */
	private List<SysDept> getChildList(List<SysDept> list, SysDept t) {
		List<SysDept> tlist = new ArrayList<>();
		Iterator<SysDept> it = list.iterator();
		while (it.hasNext()) {
			SysDept n = (SysDept) it.next();
			if (StringUtils.isNotNull(n.getParentId()) && n.getParentId().longValue() == t.getDeptId().longValue()) {
				tlist.add(n);
			}
		}
		return tlist;
	}

	/**
	 * 判断是否有子节点
	 */
	private boolean hasChild(List<SysDept> list, SysDept t) {
		return getChildList(list, t).size() > 0;
	}
}
