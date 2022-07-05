package com.snow.web.controller.system;

import com.snow.common.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.snow.common.constant.Constants;
import com.snow.common.core.domain.AjaxResult;
import com.snow.common.core.domain.entity.SysUser;
import com.snow.common.core.domain.model.LoginBody;
import com.snow.web.service.SysLoginService;
import com.snow.web.service.SysPermissionService;
import com.snow.web.service.ISysMenuService;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 登录验证
 *
 * @author ruoyi
 */
@RestController
public class SysLoginController extends BaseController {
	@Autowired
	private SysLoginService loginService;

	@Autowired
	private ISysMenuService menuService;

	@Autowired
	private SysPermissionService permissionService;

	/**
	 * 登录方法
	 *
	 * @param loginBody 登录信息
	 * @return 结果
	 */
	@PostMapping("/login")
	public Mono<AjaxResult> login(ServerWebExchange exchange, @RequestBody LoginBody loginBody) {
		return Mono.defer(() -> {
			// 生成令牌
			return loginService.login(exchange, loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
					loginBody.getUuid())
				.map(token -> {
					return AjaxResult.success()
						.put(Constants.TOKEN, token);
				});
		});
	}

	/**
	 * 获取用户信息
	 *
	 * @return 用户信息
	 */
	@GetMapping("getInfo")
	public Mono<AjaxResult> getInfo() {
		return startLoginUserMono(loginUser -> {
			SysUser user = loginUser.getUser();
			// 角色集合
			return permissionService.getRolePermission(user).flatMap(roles -> {
				return permissionService.getMenuPermission(user).map(permissions -> {
					// 权限集合
					return AjaxResult.success()
						.put("user", user)
						.put("roles", roles)
						.put("permissions", permissions);
				});
			});
		});
	}

	/**
	 * 获取路由信息
	 *
	 * @return 路由信息
	 */
	@GetMapping("getRouters")
	public Mono<AjaxResult> getRouters() {
		return startLoginUserMono(loginUser -> {
			Long userId = loginUser.getUserId();
			return menuService.selectMenuTreeByUserId(userId)
				.map(menus -> menuService.buildMenus(menus))
				.map(AjaxResult::success);
		});
	}
}
