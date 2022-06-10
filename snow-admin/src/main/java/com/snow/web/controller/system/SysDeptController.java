package com.snow.web.controller.system;

import com.snow.common.annotation.Log;
import com.snow.common.biz.EntityController;
import com.snow.common.constant.UserConstants;
import com.snow.common.core.domain.AjaxResult;
import com.snow.common.core.domain.entity.SysDept;
import com.snow.common.enums.BusinessType;
import com.snow.common.exception.ServiceException;
import com.snow.common.utils.StringUtils;
import com.snow.system.service.ISysDeptService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Iterator;

/**
 * 部门信息
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/dept")
public class SysDeptController extends EntityController<SysDept, Long, ISysDeptService> {

	/**
	 * 获取部门列表
	 */
	@PreAuthorize("@ss.hasPermi('system:dept:list')")
	@GetMapping("/list")
	public Mono<AjaxResult> list(SysDept dept) {
		return startMono(() -> {
			return service.selectList(dept).map(AjaxResult::success);
		});
	}

	/**
	 * 查询部门列表（排除节点）
	 */
	@PreAuthorize("@ss.hasPermi('system:dept:list')")
	@GetMapping("/list/exclude/{deptId}")
	public Mono<AjaxResult> excludeChild(@PathVariable(value = "deptId", required = false) Long deptId) {
		return startMono(() -> {
			return service.selectList(new SysDept()).map(depts -> {
				Iterator<SysDept> it = depts.iterator();
				while (it.hasNext()) {
					SysDept d = (SysDept) it.next();
					if (d.getDeptId().intValue() == deptId
						|| ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), deptId + "")) {
						it.remove();
					}
				}
				return AjaxResult.success(depts);
			});

		});
	}

	/**
	 * 根据部门编号获取详细信息
	 */
	@PreAuthorize("@ss.hasPermi('system:dept:query')")
	@GetMapping(value = "/{deptId}")
	public Mono<AjaxResult> getInfo(@PathVariable Long deptId) {
		return startMono(() -> {
			return service.checkDeptDataScope(deptId).flatMap(o -> {
				return service.selectById(deptId).map(AjaxResult::success);
			});
		});
	}

	/**
	 * 获取部门下拉树列表
	 */
	@GetMapping("/treeselect")
	public Mono<AjaxResult> treeselect(SysDept dept) {
		return startMono(() -> {
			return service.selectList(dept)
				.map(depts -> service.buildDeptTreeSelect(depts))
				.map(AjaxResult::success);
		});
	}

	/**
	 * 加载对应角色部门列表树
	 */
	@GetMapping(value = "/roleDeptTreeselect/{roleId}")
	public Mono<AjaxResult> roleDeptTreeselect(@PathVariable("roleId") Long roleId) {
		return startMono(() -> {
			return service.selectList(new SysDept()).flatMap(depts -> {
				return service.selectDeptListByRoleId(roleId).map(keys -> {
					AjaxResult ajax = AjaxResult.success();
					ajax.put("checkedKeys", keys);
					ajax.put("depts", service.buildDeptTreeSelect(depts));
					return ajax;
				});
			});
		});
	}

	/**
	 * 新增部门
	 */
	@PreAuthorize("@ss.hasPermi('system:dept:add')")
	@Log(title = "部门管理", businessType = BusinessType.INSERT)
	@PostMapping
	public Mono<AjaxResult> add(@Validated @RequestBody SysDept dept) {
		return startLoginUserMono(loginUser -> {
			return service.checkDeptNameUnique(dept)
				.flatMap(unique -> {
					if (UserConstants.NOT_UNIQUE.equals(unique)) {
						return AjaxResult.errorMono("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
					}
					dept.setCreateBy(loginUser.getUsername());
					return service.insert(dept).map(this::toAjax);
				});
		});
	}

	/**
	 * 修改部门
	 */
	@PreAuthorize("@ss.hasPermi('system:dept:edit')")
	@Log(title = "部门管理", businessType = BusinessType.UPDATE)
	@PutMapping
	public Mono<AjaxResult> edit(@Validated @RequestBody SysDept dept) {
		return startLoginUserMono(loginUser -> {
			Long deptId = dept.getDeptId();
			return service.checkDeptDataScope(deptId)
				.flatMap(o -> {
					return service.checkDeptNameUnique(dept);
				}).flatMap(unique -> {
					if (UserConstants.NOT_UNIQUE.equals(unique)) {
						throw new ServiceException("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
					} else if (dept.getParentId().equals(deptId)) {
						throw new ServiceException("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
					}
					return service.selectNormalChildrenDeptById(deptId);
				}).flatMap(count -> {
					if (StringUtils.equals(UserConstants.DEPT_DISABLE, dept.getStatus()) && count > 0) {
						throw new ServiceException("该部门包含未停用的子部门！");
					}
					dept.setUpdateBy(loginUser.getUsername());
					return service.update(dept).map(this::toAjax);
				});
		});
	}

	/**
	 * 删除部门
	 */
	@PreAuthorize("@ss.hasPermi('system:dept:remove')")
	@Log(title = "部门管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{deptId}")
	public Mono<AjaxResult> remove(@PathVariable Long deptId) {
		return startMono(() -> {
			return service.hasChildByDeptId(deptId).flatMap(hasChild -> {
				if (hasChild) {
					return AjaxResult.errorMono("存在下级部门,不允许删除");
				}
				return service.checkDeptExistUser(deptId).flatMap(hasUser -> {
					if (hasUser) {
						return AjaxResult.errorMono("部门存在用户,不允许删除");
					}
					return service.checkDeptDataScope(deptId).flatMap(o -> {
						return service.deleteById(deptId).map(this::toAjax);
					});
				});
			});
		});
	}
}
