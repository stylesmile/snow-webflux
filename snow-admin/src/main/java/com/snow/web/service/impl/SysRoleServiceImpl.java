package com.snow.web.service.impl;

import com.snow.common.annotation.DataScope;
import com.snow.common.biz.BaseServiceImpl;
import com.snow.common.constant.UserConstants;
import com.snow.common.core.domain.entity.SysRole;
import com.snow.common.core.domain.entity.SysUser;
import com.snow.common.exception.ServiceException;
import com.snow.common.utils.SecurityUtils;
import com.snow.common.utils.StringUtils;
import com.snow.common.utils.spring.SpringUtils;
import com.snow.web.domain.SysRoleDept;
import com.snow.web.domain.SysRoleMenu;
import com.snow.web.domain.SysUserRole;
import com.snow.web.mapper.SysRoleDeptMapper;
import com.snow.web.mapper.SysRoleMapper;
import com.snow.web.mapper.SysRoleMenuMapper;
import com.snow.web.mapper.SysUserRoleMapper;
import com.snow.web.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * 角色 业务层处理
 *
 * @author ruoyi
 */
@Service
public class SysRoleServiceImpl
	extends BaseServiceImpl<SysRole, Long, SysRoleMapper>
	implements ISysRoleService {

	@Autowired
	private SysRoleMenuMapper roleMenuMapper;

	@Autowired
	private SysUserRoleMapper userRoleMapper;

	@Autowired
	private SysRoleDeptMapper roleDeptMapper;

	/**
	 * 根据条件分页查询角色数据
	 *
	 * @param role 角色信息
	 * @return 角色数据集合信息
	 */
	@Override
	@DataScope(deptAlias = "d")
	public Mono<List<SysRole>> selectList(SysRole role) {
		return super.selectList(role);
	}

	/**
	 * 根据用户ID查询角色
	 *
	 * @param userId 用户ID
	 * @return 角色列表
	 */
	@Override
	public Mono<List<SysRole>> selectRolesByUserId(Long userId) {
		return mapper.selectRolePermissionByUserId(userId)
			.collectList()
			.flatMap(userRoles -> {
				return selectRoleAll().map(roles -> {
					for (SysRole role : roles) {
						for (SysRole userRole : userRoles) {
							if (role.getRoleId().longValue() == userRole.getRoleId().longValue()) {
								role.setFlag(true);
								break;
							}
						}
					}
					return roles;
				});
			});
	}

	/**
	 * 根据用户ID查询权限
	 *
	 * @param userId 用户ID
	 * @return 权限列表
	 */
	@Override
	public Mono<Set<String>> selectRolePermissionByUserId(Long userId) {
		return mapper.selectRolePermissionByUserId(userId).collectList().map(perms -> {
			Set<String> permsSet = new HashSet<>();
			for (SysRole perm : perms) {
				if (StringUtils.isNotNull(perm)) {
					permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(",")));
				}
			}
			return permsSet;
		});
	}

	/**
	 * 查询所有角色
	 *
	 * @return 角色列表
	 */
	@Override
	public Mono<List<SysRole>> selectRoleAll() {
		return SpringUtils.getAopProxy(this).selectList(new SysRole());
	}

	/**
	 * 根据用户ID获取角色选择框列表
	 *
	 * @param userId 用户ID
	 * @return 选中角色ID列表
	 */
	@Override
	public Mono<List<Long>> selectRoleListByUserId(Long userId) {
		return mapper.selectRoleListByUserId(userId).collectList();
	}

	/**
	 * 校验角色名称是否唯一
	 *
	 * @param role 角色信息
	 * @return 结果
	 */
	@Override
	public Mono<String> checkRoleNameUnique(SysRole role) {
		Long roleId = StringUtils.isNull(role.getRoleId()) ? -1L : role.getRoleId();
		return mapper.checkRoleNameUnique(role.getRoleName()).map(info -> {
			if (StringUtils.isNotNull(info) && info.getRoleId().longValue() != roleId.longValue()) {
				return UserConstants.NOT_UNIQUE;
			}
			return UserConstants.UNIQUE;
		});

	}

	/**
	 * 校验角色权限是否唯一
	 *
	 * @param role 角色信息
	 * @return 结果
	 */
	@Override
	public Mono<String> checkRoleKeyUnique(SysRole role) {
		Long roleId = StringUtils.isNull(role.getRoleId()) ? -1L : role.getRoleId();
		return mapper.checkRoleKeyUnique(role.getRoleKey()).map(info -> {
			if (StringUtils.isNotNull(info) && info.getRoleId().longValue() != roleId.longValue()) {
				return UserConstants.NOT_UNIQUE;
			}
			return UserConstants.UNIQUE;
		});
	}

	/**
	 * 校验角色是否允许操作
	 *
	 * @param role 角色信息
	 */
	@Override
	public void checkRoleAllowed(SysRole role) {
		if (StringUtils.isNotNull(role.getRoleId()) && role.isAdmin()) {
			throw new ServiceException("不允许操作超级管理员角色");
		}
	}

	/**
	 * 校验角色是否有数据权限
	 *
	 * @param roleId 角色id
	 * @return
	 */
	@Override
	public Mono<Long> checkRoleDataScope(Long roleId) {
		return SecurityUtils.getUserId().flatMap(loginUserId -> {
			if (!SysUser.isAdmin(loginUserId)) {
				SysRole role = new SysRole();
				role.setRoleId(roleId);
				return SpringUtils.getAopProxy(this).selectList(role).doOnSuccess(roles -> {
					if (StringUtils.isEmpty(roles)) {
						throw new ServiceException("没有权限访问角色数据！");
					}
				}).thenReturn(1L);
			}
			return Mono.just(1L);
		});
	}

	/**
	 * 通过角色ID查询角色使用数量
	 *
	 * @param roleId 角色ID
	 * @return 结果
	 */
	@Override
	public Mono<Long> countUserRoleByRoleId(Long roleId) {
		return userRoleMapper.countUserRoleByRoleId(roleId);
	}

	/**
	 * 新增保存角色信息
	 *
	 * @param role 角色信息
	 * @return 结果
	 */
	@Override
	@Transactional
	public Mono<Long> insert(SysRole role) {
		// 新增角色信息
		return mapper.insert(role).flatMap(rows -> {
			return insertRoleMenu(role).thenReturn(rows);
		});

	}

	/**
	 * 修改保存角色信息
	 *
	 * @param role 角色信息
	 * @return 结果
	 */
	@Override
	@Transactional
	public Mono<Long> update(SysRole role) {
		// 修改角色信息
		return mapper.update(role).flatMap(rows -> {
			// 删除角色与菜单关联
			return roleMenuMapper.deleteRoleMenuByRoleId(role.getRoleId()).flatMap(o -> {
				return insertRoleMenu(role);
			}).thenReturn(rows);
		});
	}

	/**
	 * 修改角色状态
	 *
	 * @param role 角色信息
	 * @return 结果
	 */
	@Override
	public Mono<Long> updateRoleStatus(SysRole role) {
		return mapper.update(role);
	}

	/**
	 * 修改数据权限信息
	 *
	 * @param role 角色信息
	 * @return 结果
	 */
	@Override
	@Transactional
	public Mono<Long> authDataScope(SysRole role) {
		// 修改角色信息
		return mapper.update(role).flatMap(rows -> {
			// 删除角色与部门关联
			return roleDeptMapper.deleteRoleDeptByRoleId(role.getRoleId()).flatMap(o -> {
				// 新增角色和部门信息（数据权限）
				return insertRoleDept(role);
			}).thenReturn(rows);
		});
	}

	/**
	 * 新增角色菜单信息
	 *
	 * @param role 角色对象
	 */
	public Mono<Long> insertRoleMenu(SysRole role) {

		// 新增用户与角色管理
		List<SysRoleMenu> list = new ArrayList<>();
		for (Long menuId : role.getMenuIds()) {
			SysRoleMenu rm = new SysRoleMenu();
			rm.setRoleId(role.getRoleId());
			rm.setMenuId(menuId);
			list.add(rm);
		}
		if (list.size() > 0) {
			return roleMenuMapper.batchRoleMenu(list);
		}
		return Mono.just(1L);
	}

	/**
	 * 新增角色部门信息(数据权限)
	 *
	 * @param role 角色对象
	 */
	public Mono<Long> insertRoleDept(SysRole role) {

		// 新增角色与部门（数据权限）管理
		List<SysRoleDept> list = new ArrayList<>();
		for (Long deptId : role.getDeptIds()) {
			SysRoleDept rd = new SysRoleDept();
			rd.setRoleId(role.getRoleId());
			rd.setDeptId(deptId);
			list.add(rd);
		}
		if (list.size() > 0) {
			return roleDeptMapper.batchRoleDept(list);
		}
		return Mono.just(1L);
	}

	/**
	 * 通过角色ID删除角色
	 *
	 * @param roleId 角色ID
	 * @return 结果
	 */
	@Override
	@Transactional
	public Mono<Long> deleteById(Long roleId) {
		// 删除角色与菜单关联
		return roleMenuMapper.deleteRoleMenuByRoleId(roleId).flatMap(o -> {
			// 删除角色与部门关联
			return roleDeptMapper.deleteRoleDeptByRoleId(roleId);
		}).flatMap(o -> {
			return mapper.deleteById(roleId);
		});
	}

	/**
	 * 批量删除角色信息
	 *
	 * @param roleIds 需要删除的角色ID
	 * @return 结果
	 */
	@Override
	@Transactional
	public Mono<Long> deleteByIds(Long[] roleIds) {
		return Flux.fromArray(roleIds).flatMap(roleId -> {
				checkRoleAllowed(new SysRole(roleId));
				return checkRoleDataScope(roleId).thenReturn(roleId);
			}).flatMap(roleId -> {
				return selectById(roleId).flatMap(role -> {
					return countUserRoleByRoleId(roleId).doOnSuccess(count -> {
						if ((roleId) > 0) {
							throw new ServiceException(String.format("%1$s已分配,不能删除", role.getRoleName()));
						}
					});
				});
			}).count()
			.flatMap(o -> {
				// 删除角色与菜单关联
				return roleMenuMapper.deleteRoleMenu(roleIds);
			}).flatMap(o -> {
				// 删除角色与部门关联
				return roleDeptMapper.deleteRoleDept(roleIds);
			}).flatMap(o -> {
				return mapper.deleteByIds(roleIds);
			});
	}

	/**
	 * 取消授权用户角色
	 *
	 * @param userRole 用户和角色关联信息
	 * @return 结果
	 */
	@Override
	public Mono<Long> deleteAuthUser(SysUserRole userRole) {
		return userRoleMapper.deleteUserRoleInfo(userRole);
	}

	/**
	 * 批量取消授权用户角色
	 *
	 * @param roleId  角色ID
	 * @param userIds 需要取消授权的用户数据ID
	 * @return 结果
	 */
	@Override
	public Mono<Long> deleteAuthUsers(Long roleId, Long[] userIds) {
		return userRoleMapper.deleteUserRoleInfos(roleId, userIds);
	}

	/**
	 * 批量选择授权用户角色
	 *
	 * @param roleId  角色ID
	 * @param userIds 需要授权的用户数据ID
	 * @return 结果
	 */
	@Override
	public Mono<Long> insertAuthUsers(Long roleId, Long[] userIds) {
		// 新增用户与角色管理
		List<SysUserRole> list = new ArrayList<>();
		for (Long userId : userIds) {
			SysUserRole ur = new SysUserRole();
			ur.setUserId(userId);
			ur.setRoleId(roleId);
			list.add(ur);
		}
		return userRoleMapper.batchUserRole(list);
	}
}
