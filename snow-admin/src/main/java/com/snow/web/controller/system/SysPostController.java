package com.snow.web.controller.system;

import com.snow.common.annotation.Log;
import com.snow.common.biz.EntityController;
import com.snow.common.constant.UserConstants;
import com.snow.common.core.domain.AjaxResult;
import com.snow.common.core.page.TableDataInfo;
import com.snow.common.enums.BusinessType;
import com.snow.common.exception.ServiceException;
import com.snow.web.domain.SysPost;
import com.snow.web.service.ISysPostService;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 岗位信息操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/post")
public class SysPostController extends EntityController<SysPost, Long, ISysPostService> {

	/**
	 * 获取岗位列表
	 */
	@PreAuthorize("@ss.hasPermi('system:post:list')")
	@GetMapping("/list")
	public Mono<TableDataInfo> list(ServerWebExchange exchange, SysPost post) {
		return super.list(exchange, post);
	}

	@Log(title = "岗位管理", businessType = BusinessType.EXPORT)
	@PreAuthorize("@ss.hasPermi('system:post:export')")
	@PostMapping("/export")
	public Mono<Void> export(ServerHttpResponse response, SysPost post) {
		return super.export(response, post, SysPost.class, "岗位数据");
	}

	/**
	 * 根据岗位编号获取详细信息
	 */
	@PreAuthorize("@ss.hasPermi('system:post:query')")
	@GetMapping(value = "/{id}")
	public Mono<AjaxResult> getInfo(@PathVariable long id) {
		return super.getInfo(id);
	}

	/**
	 * 新增岗位
	 */
	@PreAuthorize("@ss.hasPermi('system:post:add')")
	@Log(title = "岗位管理", businessType = BusinessType.INSERT)
	@PostMapping
	public Mono<AjaxResult> add(@Validated @RequestBody SysPost post) {
		return startLoginUserMono(loginUser -> {
			return service.checkPostNameUnique(post)
				.flatMap(unique -> {
					if (UserConstants.NOT_UNIQUE.equals(unique)) {
						throw new ServiceException("新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
					}
					return service.checkPostCodeUnique(post);
				}).flatMap(unique -> {
					if (UserConstants.NOT_UNIQUE.equals(unique)) {
						return AjaxResult.errorMono("新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
					}
					post.setCreateBy(loginUser.getUsername());
					return service.insert(post).map(this::toAjax);
				});
		});
	}

	/**
	 * 修改岗位
	 */
	@PreAuthorize("@ss.hasPermi('system:post:edit')")
	@Log(title = "岗位管理", businessType = BusinessType.UPDATE)
	@PutMapping
	public Mono<AjaxResult> edit(@Validated @RequestBody SysPost post) {
		return startLoginUserMono(loginUser -> {
			return service.checkPostNameUnique(post)
				.flatMap(unique -> {
					if (UserConstants.NOT_UNIQUE.equals(unique)) {
						throw new ServiceException("修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
					}
					return service.checkPostCodeUnique(post);
				}).flatMap(unique -> {
					if (UserConstants.NOT_UNIQUE.equals(unique)) {
						return AjaxResult.errorMono("修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
					}
					post.setUpdateBy(loginUser.getUsername());
					return service.update(post).map(this::toAjax);
				});
		});
	}

	/**
	 * 删除岗位
	 */
	@PreAuthorize("@ss.hasPermi('system:post:remove')")
	@Log(title = "岗位管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{postIds}")
	public Mono<AjaxResult> remove(@PathVariable Long[] postIds) {
		return startMono(() -> {
			return service.deleteByIds(postIds).map(this::toAjax);
		});
	}

	/**
	 * 获取岗位选择框列表
	 */
	@GetMapping("/optionselect")
	public Mono<AjaxResult> optionselect() {
		return startMono(() -> {
			return service.selectPostAll().map(AjaxResult::success);
		});
	}
}
