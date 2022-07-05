package com.snow.web.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.snow.common.core.domain.entity.SysUser;
import reactor.core.publisher.Mono;

/**
 * 用户权限处理
 *
 * @author ruoyi
 */
@Component
public class SysPermissionService {
	@Autowired
	private ISysRoleService roleService;

	@Autowired
	private ISysMenuService menuService;

	/**
	 * 获取角色数据权限
	 *
	 * @param user 用户信息
	 * @return 角色权限信息
	 */
	public Mono<Set<String>> getRolePermission(SysUser user) {

		// 管理员拥有所有权限
		if (user.isAdmin()) {
			Set<String> roles = new HashSet<>();
			roles.add("admin");
			return Mono.just(roles);
		} else {
			return roleService.selectRolePermissionByUserId(user.getUserId()).map(permissions -> {
				Set<String> roles = new HashSet<>();
				roles.addAll(permissions);
				return roles;
			});
		}
	}

	/**
	 * 获取菜单数据权限
	 *
	 * @param user 用户信息
	 * @return 菜单权限信息
	 */
	public Mono<Set<String>> getMenuPermission(SysUser user) {

		// 管理员拥有所有权限
		if (user.isAdmin()) {
			Set<String> perms = new HashSet<>();
			perms.add("*:*:*");
			return Mono.just(perms);
		} else {
			return menuService.selectMenuPermsByUserId(user.getUserId()).map(permissions -> {
				Set<String> perms = new HashSet<>();
				perms.addAll(permissions);
				return perms;
			});
		}
	}
}
