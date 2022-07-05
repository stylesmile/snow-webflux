package com.snow.web.controller.system;

import com.snow.common.biz.EntityController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.snow.common.annotation.Log;
import com.snow.common.constant.UserConstants;
import com.snow.common.core.domain.AjaxResult;
import com.snow.common.core.domain.entity.SysMenu;
import com.snow.common.enums.BusinessType;
import com.snow.common.utils.StringUtils;
import com.snow.web.service.ISysMenuService;
import reactor.core.publisher.Mono;

/**
 * 菜单信息
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/menu")
public class SysMenuController extends EntityController<SysMenu, Long, ISysMenuService> {

	/**
	 * 获取菜单列表
	 */
	@PreAuthorize("@ss.hasPermi('system:menu:list')")
	@GetMapping("/list")
	public Mono<AjaxResult> list(SysMenu menu) {
		return startLoginUserMono(loginUser -> {
			return service.selectMenuList(menu, loginUser.getUserId()).map(AjaxResult::success);
		});
	}

	/**
	 * 根据菜单编号获取详细信息
	 */
	@PreAuthorize("@ss.hasPermi('system:menu:query')")
	@GetMapping(value = "/{menuId}")
	public Mono<AjaxResult> getInfo(@PathVariable Long menuId) {
		return super.getInfo(menuId);
	}

	/**
	 * 获取菜单下拉树列表
	 */
	@GetMapping("/treeselect")
	public Mono<AjaxResult> treeselect(SysMenu menu) {
		return startLoginUserMono(loginUser -> {
			return service.selectMenuList(menu, loginUser.getUserId())
				.map(menus -> service.buildMenuTreeSelect(menus))
				.map(AjaxResult::success);
		});
	}

	/**
	 * 加载对应角色菜单列表树
	 */
	@GetMapping(value = "/roleMenuTreeselect/{roleId}")
	public Mono<AjaxResult> roleMenuTreeselect(@PathVariable("roleId") Long roleId) {
		return startLoginUserMono(loginUser -> {
			return service.selectMenuList(loginUser.getUserId()).flatMap(menus -> {
				return service.selectMenuListByRoleId(roleId).map(keys -> {
					AjaxResult ajax = AjaxResult.success();
					ajax.put("checkedKeys", keys);
					ajax.put("menus", service.buildMenuTreeSelect(menus));
					return ajax;
				});
			});
		});
	}

	/**
	 * 新增菜单
	 */
	@PreAuthorize("@ss.hasPermi('system:menu:add')")
	@Log(title = "菜单管理", businessType = BusinessType.INSERT)
	@PostMapping
	public Mono<AjaxResult> add(@Validated @RequestBody SysMenu menu) {
		return startLoginUserMono(loginUser -> {
			return service.checkMenuNameUnique(menu)
				.flatMap(unique -> {
					if (UserConstants.NOT_UNIQUE.equals(unique)) {
						return AjaxResult.errorMono("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
					} else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath())) {
						return AjaxResult.errorMono("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
					}
					menu.setCreateBy(loginUser.getUsername());
					return service.insert(menu).map(this::toAjax);
				});
		});
	}

	/**
	 * 修改菜单
	 */
	@PreAuthorize("@ss.hasPermi('system:menu:edit')")
	@Log(title = "菜单管理", businessType = BusinessType.UPDATE)
	@PutMapping
	public Mono<AjaxResult> edit(@Validated @RequestBody SysMenu menu) {
		return startLoginUserMono(loginUser -> {
			return service.checkMenuNameUnique(menu)
				.flatMap(unique -> {
					if (UserConstants.NOT_UNIQUE.equals(unique)) {
						return AjaxResult.errorMono("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
					} else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath())) {
						return AjaxResult.errorMono("修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
					} else if (menu.getMenuId().equals(menu.getParentId())) {
						return AjaxResult.errorMono("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
					}
					menu.setUpdateBy(loginUser.getUsername());
					return service.update(menu).map(this::toAjax);
				});
		});
	}

	/**
	 * 删除菜单
	 */
	@PreAuthorize("@ss.hasPermi('system:menu:remove')")
	@Log(title = "菜单管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{menuId}")
	public Mono<AjaxResult> remove(@PathVariable("menuId") Long menuId) {
		return startMono(() -> {
			return service.hasChildByMenuId(menuId).flatMap(hasSubMenu -> {
				if (hasSubMenu) {
					return AjaxResult.errorMono("存在子菜单,不允许删除");
				}
				return service.checkMenuExistRole(menuId).flatMap(hasRole -> {
					if (hasRole) {
						return AjaxResult.errorMono("菜单已分配,不允许删除");
					}
					return service.deleteById(menuId).map(this::toAjax);
				});
			});
		});
	}
}