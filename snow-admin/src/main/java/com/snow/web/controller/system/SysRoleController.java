package com.snow.web.controller.system;

import com.snow.common.annotation.Log;
import com.snow.common.biz.EntityController;
import com.snow.common.constant.UserConstants;
import com.snow.common.core.domain.AjaxResult;
import com.snow.common.core.domain.entity.SysRole;
import com.snow.common.core.domain.entity.SysUser;
import com.snow.common.core.page.TableDataInfo;
import com.snow.common.enums.BusinessType;
import com.snow.common.exception.ServiceException;
import com.snow.common.utils.StringUtils;
import com.snow.web.service.SysPermissionService;
import com.snow.web.service.TokenService;
import com.snow.web.domain.SysUserRole;
import com.snow.web.service.ISysRoleService;
import com.snow.web.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 角色信息
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/role")
public class SysRoleController extends EntityController<SysRole, Long, ISysRoleService> {

	@Autowired
	private TokenService tokenService;

	@Autowired
	private SysPermissionService permissionService;

	@Autowired
	private ISysUserService userService;

	@PreAuthorize("@ss.hasPermi('system:role:list')")
	@GetMapping("/list")
	public Mono<TableDataInfo> list(ServerWebExchange exchange, SysRole role) {
		return super.list(exchange, role);
	}

	@Log(title = "角色管理", businessType = BusinessType.EXPORT)
	@PreAuthorize("@ss.hasPermi('system:role:export')")
	@PostMapping("/export")
	public Mono<Void> export(ServerHttpResponse response, SysRole role) {
		return super.export(response, role, SysRole.class, "角色数据");
	}

	/**
	 * 根据角色编号获取详细信息
	 */
	@PreAuthorize("@ss.hasPermi('system:role:query')")
	@GetMapping(value = "/{roleId}")
	public Mono<AjaxResult> getInfo(@PathVariable Long roleId) {
		return startMono(() -> {
			return service.checkRoleDataScope(roleId);
		}).flatMap(o -> {
			return service.selectById(roleId).map(AjaxResult::success);
		});
	}

	/**
	 * 新增角色
	 */
	@PreAuthorize("@ss.hasPermi('system:role:add')")
	@Log(title = "角色管理", businessType = BusinessType.INSERT)
	@PostMapping
	public Mono<AjaxResult> add(@Validated @RequestBody SysRole role) {
		return startLoginUserMono(loginUser -> {
			return service.checkRoleNameUnique(role
			).flatMap(unique -> {
				if (UserConstants.NOT_UNIQUE.equals(unique)) {
					throw new ServiceException("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
				}
				return service.checkRoleKeyUnique(role);
			}).flatMap(unique -> {
				if (UserConstants.NOT_UNIQUE.equals(unique)) {
					return AjaxResult.errorMono("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
				}
				role.setCreateBy(loginUser.getUsername());
				return service.insert(role).map(this::toAjax);
			});
		});
	}

	/**
	 * 修改保存角色
	 */
	@PreAuthorize("@ss.hasPermi('system:role:edit')")
	@Log(title = "角色管理", businessType = BusinessType.UPDATE)
	@PutMapping
	public Mono<AjaxResult> edit(@Validated @RequestBody SysRole role) {
		return startLoginUserMono(loginUser -> {
			service.checkRoleAllowed(role);
			return service.checkRoleDataScope(role.getRoleId())
				.flatMap(o -> {
					return service.checkRoleNameUnique(role);
				}).flatMap(unique -> {
					if (UserConstants.NOT_UNIQUE.equals(unique)) {
						throw new ServiceException("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
					}
					return service.checkRoleKeyUnique(role);
				}).flatMap(unique -> {
					if (UserConstants.NOT_UNIQUE.equals(unique)) {
						throw new ServiceException("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
					}
					role.setUpdateBy(loginUser.getUsername());
					return service.update(role);
				}).flatMap(rows -> {
					if (rows > 0) {
						// 更新缓存用户权限
						if (StringUtils.isNotNull(loginUser.getUser()) && !loginUser.getUser().isAdmin()) {
							return permissionService.getMenuPermission(loginUser.getUser()).flatMap(perms -> {
								loginUser.setPermissions(perms);
								return userService.selectUserByUserName(loginUser.getUser().getUserName());
							}).doOnSuccess(user -> {
								loginUser.setUser(user);
								tokenService.setLoginUser(loginUser);
							}).thenReturn(AjaxResult.success());
						}
						return AjaxResult.successMono();
					}
					return AjaxResult.errorMono("修改角色'" + role.getRoleName() + "'失败，请联系管理员");
				});
		});
	}

	/**
	 * 修改保存数据权限
	 */
	@PreAuthorize("@ss.hasPermi('system:role:edit')")
	@Log(title = "角色管理", businessType = BusinessType.UPDATE)
	@PutMapping("/dataScope")
	public Mono<AjaxResult> dataScope(@RequestBody SysRole role) {
		return startMono(() -> {
			service.checkRoleAllowed(role);
			return service.checkRoleDataScope(role.getRoleId());
		}).flatMap(o -> {
			return service.authDataScope(role).map(this::toAjax);
		});
	}

	/**
	 * 状态修改
	 */
	@PreAuthorize("@ss.hasPermi('system:role:edit')")
	@Log(title = "角色管理", businessType = BusinessType.UPDATE)
	@PutMapping("/changeStatus")
	public Mono<AjaxResult> changeStatus(@RequestBody SysRole role) {
		return startLoginUserMono(loginUser -> {
			service.checkRoleAllowed(role);
			return service.checkRoleDataScope(role.getRoleId())
				.flatMap(o -> {
					role.setUpdateBy(loginUser.getUsername());
					return service.updateRoleStatus(role).map(this::toAjax);
				});
		});
	}

	/**
	 * 删除角色
	 */
	@PreAuthorize("@ss.hasPermi('system:role:remove')")
	@Log(title = "角色管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{roleIds}")
	public Mono<AjaxResult> remove(@PathVariable Long[] roleIds) {
		return startMono(() -> {
			return service.deleteByIds(roleIds).map(this::toAjax);
		});
	}

	/**
	 * 获取角色选择框列表
	 */
	@PreAuthorize("@ss.hasPermi('system:role:query')")
	@GetMapping("/optionselect")
	public Mono<AjaxResult> optionselect() {
		return startMono(() -> {
			return service.selectRoleAll().map(AjaxResult::success);
		});
	}

	/**
	 * 查询已分配用户角色列表
	 */
	@PreAuthorize("@ss.hasPermi('system:role:list')")
	@GetMapping("/authUser/allocatedList")
	public Mono<TableDataInfo> allocatedList(ServerWebExchange exchange, SysUser cond) {
		return startMono(() -> {
			startPage(exchange.getRequest(), cond);
			return userService.selectAllocatedList(cond);
		});
	}

	/**
	 * 查询未分配用户角色列表
	 */
	@PreAuthorize("@ss.hasPermi('system:role:list')")
	@GetMapping("/authUser/unallocatedList")
	public Mono<TableDataInfo> unallocatedList(ServerWebExchange exchange, SysUser cond) {
		return startMono(() -> {
			startPage(exchange.getRequest(), cond);
			return userService.selectUnallocatedList(cond);
		});
	}

	/**
	 * 取消授权用户
	 */
	@PreAuthorize("@ss.hasPermi('system:role:edit')")
	@Log(title = "角色管理", businessType = BusinessType.GRANT)
	@PutMapping("/authUser/cancel")
	public Mono<AjaxResult> cancelAuthUser(@RequestBody SysUserRole userRole) {
		return startMono(() -> {
			return service.deleteAuthUser(userRole).map(this::toAjax);
		});
	}

	/**
	 * 批量取消授权用户
	 */
	@PreAuthorize("@ss.hasPermi('system:role:edit')")
	@Log(title = "角色管理", businessType = BusinessType.GRANT)
	@PutMapping("/authUser/cancelAll")
	public Mono<AjaxResult> cancelAuthUserAll(Long roleId, Long[] userIds) {
		return startMono(() -> {
			return service.deleteAuthUsers(roleId, userIds).map(this::toAjax);
		});
	}

	/**
	 * 批量选择用户授权
	 */
	@PreAuthorize("@ss.hasPermi('system:role:edit')")
	@Log(title = "角色管理", businessType = BusinessType.GRANT)
	@PutMapping("/authUser/selectAll")
	public Mono<AjaxResult> selectAuthUserAll(Long roleId, Long[] userIds) {
		return startMono(() -> {
			return service.checkRoleDataScope(roleId);
		}).flatMap(o -> {
			return service.insertAuthUsers(roleId, userIds).map(this::toAjax);
		});
	}
}
