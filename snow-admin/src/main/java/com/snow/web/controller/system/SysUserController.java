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
import com.snow.common.utils.SecurityUtils;
import com.snow.common.utils.StringUtils;
import com.snow.common.utils.poi.ExcelUtil;
import com.snow.system.service.ISysPostService;
import com.snow.system.service.ISysRoleService;
import com.snow.system.service.ISysUserService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户信息
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController extends EntityController<SysUser, Long, ISysUserService> {
	@Autowired
	private ISysRoleService roleService;

	@Autowired
	private ISysPostService postService;

	/**
	 * 获取用户列表
	 */
	@PreAuthorize("@ss.hasPermi('system:user:list')")
	@GetMapping("/list")
	public Mono<TableDataInfo> list(ServerWebExchange exchange, SysUser user) {
		return super.list(exchange, user);
	}

	@Log(title = "用户管理", businessType = BusinessType.EXPORT)
	@PreAuthorize("@ss.hasPermi('system:user:export')")
	@PostMapping("/export")
	public Mono<Void> export(ServerHttpResponse response, SysUser user) {
		return super.export(response, user, SysUser.class, "用户数据");
	}

	@Log(title = "用户管理", businessType = BusinessType.IMPORT)
	@PreAuthorize("@ss.hasPermi('system:user:import')")
	@PostMapping("/importData")
	public Mono<AjaxResult> importData(MultipartFile file, boolean updateSupport) {
		return startLoginUserMono(loginUser -> {
			ExcelUtil<SysUser> util = new ExcelUtil<>(SysUser.class);
			List<SysUser> userList = null;
			try {
				userList = util.importExcel(file.getInputStream());
			} catch (Exception e) {
				return Mono.error(e);
			}
			String operName = loginUser.getUsername();
			return service.importUser(userList, updateSupport, operName)
				.map(AjaxResult::success);
		});
	}

	@PostMapping("/importTemplate")
	public Mono<Void> importTemplate(ServerHttpResponse response) {
		return startVoid(() -> {
			ExcelUtil<SysUser> util = new ExcelUtil<>(SysUser.class);
			util.importTemplateExcel(response, "用户数据");
			return Mono.just(9);
		});
	}

	/**
	 * 根据用户编号获取详细信息
	 */
	@PreAuthorize("@ss.hasPermi('system:user:query')")
	@GetMapping(value = {"/", "/{userId}"})
	public Mono<AjaxResult> getInfo(@PathVariable(value = "userId", required = false) Long userId) {
		return startMono(() -> {
			return service.checkUserDataScope(userId);
		}).flatMap(o -> {
			return roleService.selectRoleAll();
		}).flatMap(roles -> {
			return postService.selectPostAll().flatMap(posts -> {
				AjaxResult ajax = AjaxResult.success();
				ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
				ajax.put("posts", posts);
				if (StringUtils.isNotNull(userId)) {
					return service.selectById(userId).flatMap(sysUser -> {
						ajax.put(AjaxResult.DATA_TAG, sysUser);
						return postService.selectPostListByUserId(userId).map(postIds -> {
							ajax.put("postIds", postIds);
							ajax.put("roleIds", sysUser.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList()));
							return ajax;
						});
					});
				}
				return Mono.just(ajax);
			});
		});
	}

	/**
	 * 新增用户
	 */
	@PreAuthorize("@ss.hasPermi('system:user:add')")
	@Log(title = "用户管理", businessType = BusinessType.INSERT)
	@PostMapping
	public Mono<AjaxResult> add(@Validated @RequestBody SysUser user) {
		return startLoginUserMono(loginUser -> {
			return service.checkUserNameUnique(user.getUserName())
				.flatMap(unique -> {
					if (UserConstants.NOT_UNIQUE.equals(unique)) {
						throw new ServiceException("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
					}
					return service.checkPhoneUnique(user);
				}).flatMap(unique -> {
					if (StringUtils.isNotEmpty(user.getPhonenumber())
						&& UserConstants.NOT_UNIQUE.equals(unique)) {
						throw new ServiceException("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
					}
					return service.checkEmailUnique(user);
				}).flatMap(unique -> {
					if (StringUtils.isNotEmpty(user.getEmail())
						&& UserConstants.NOT_UNIQUE.equals(unique)) {
						return AjaxResult.errorMono("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
					}
					user.setCreateBy(loginUser.getUsername());
					user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
					return service.insert(user).map(this::toAjax);
				});
		});
	}

	/**
	 * 修改用户
	 */
	@PreAuthorize("@ss.hasPermi('system:user:edit')")
	@Log(title = "用户管理", businessType = BusinessType.UPDATE)
	@PutMapping
	public Mono<AjaxResult> edit(@Validated @RequestBody SysUser user) {
		return startLoginUserMono(loginUser -> {
			service.checkUserAllowed(user);
			return service.checkUserDataScope(user.getUserId())
				.flatMap(o -> {
					return service.checkPhoneUnique(user);
				}).flatMap(unique -> {
					if (StringUtils.isNotEmpty(user.getPhonenumber())
						&& UserConstants.NOT_UNIQUE.equals(unique)) {
						throw new ServiceException("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
					}
					return service.checkEmailUnique(user);
				}).flatMap(unique -> {
					if (StringUtils.isNotEmpty(user.getEmail())
						&& UserConstants.NOT_UNIQUE.equals(unique)) {
						return AjaxResult.errorMono("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
					}
					user.setUpdateBy(loginUser.getUsername());
					return service.update(user).map(this::toAjax);
				});
		});
	}

	/**
	 * 删除用户
	 */
	@PreAuthorize("@ss.hasPermi('system:user:remove')")
	@Log(title = "用户管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{userIds}")
	public Mono<AjaxResult> remove(@PathVariable Long[] userIds) {
		return startLoginUserMono(loginUser -> {
			if (ArrayUtils.contains(userIds, loginUser.getUserId())) {
				return errorMono("当前用户不能删除");
			}
			return service.deleteByIds(userIds).map(this::toAjax);
		});
	}

	/**
	 * 重置密码
	 */
	@PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
	@Log(title = "用户管理", businessType = BusinessType.UPDATE)
	@PutMapping("/resetPwd")
	public Mono<AjaxResult> resetPwd(@RequestBody SysUser user) {
		return startLoginUserMono(loginUser -> {
			service.checkUserAllowed(user);
			return service.checkUserDataScope(user.getUserId())
				.flatMap(o -> {
					user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
					user.setUpdateBy(loginUser.getUsername());
					return service.resetPwd(user).map(this::toAjax);
				});
		});
	}

	/**
	 * 状态修改
	 */
	@PreAuthorize("@ss.hasPermi('system:user:edit')")
	@Log(title = "用户管理", businessType = BusinessType.UPDATE)
	@PutMapping("/changeStatus")
	public Mono<AjaxResult> changeStatus(@RequestBody SysUser user) {
		return startLoginUserMono(loginUser -> {
			service.checkUserAllowed(user);
			return service.checkUserDataScope(user.getUserId())
				.flatMap(o -> {
					user.setUpdateBy(loginUser.getUsername());
					return service.updateUserStatus(user).map(this::toAjax);
				});
		});
	}

	/**
	 * 根据用户编号获取授权角色
	 */
	@PreAuthorize("@ss.hasPermi('system:user:query')")
	@GetMapping("/authRole/{userId}")
	public Mono<AjaxResult> authRole(@PathVariable("userId") Long userId) {
		return startMono(() -> {
			return service.selectById(userId);
		}).flatMap(user -> {
			return roleService.selectRolesByUserId(userId).map(roles -> {
				AjaxResult ajax = AjaxResult.success();
				ajax.put("user", user);
				ajax.put("roles", SysUser.isAdmin(userId)
					? roles
					: roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
				return ajax;
			});
		});
	}

	/**
	 * 用户授权角色
	 */
	@PreAuthorize("@ss.hasPermi('system:user:edit')")
	@Log(title = "用户管理", businessType = BusinessType.GRANT)
	@PutMapping("/authRole")
	public Mono<AjaxResult> insertAuthRole(Long userId, Long[] roleIds) {
		return startMono(() -> {
			return service.checkUserDataScope(userId);
		}).flatMap(o -> {
			return service.insertUserAuth(userId, roleIds).thenReturn(success());
		});
	}
}
