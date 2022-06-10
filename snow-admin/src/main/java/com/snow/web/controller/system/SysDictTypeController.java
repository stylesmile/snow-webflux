package com.snow.web.controller.system;

import com.snow.common.annotation.Log;
import com.snow.common.biz.EntityController;
import com.snow.common.constant.UserConstants;
import com.snow.common.core.domain.AjaxResult;
import com.snow.common.core.domain.entity.SysDictType;
import com.snow.common.core.page.TableDataInfo;
import com.snow.common.enums.BusinessType;
import com.snow.system.service.ISysDictTypeService;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 数据字典信息
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/dict/type")
public class SysDictTypeController extends EntityController<SysDictType, Long, ISysDictTypeService> {

	@PreAuthorize("@ss.hasPermi('system:dict:list')")
	@GetMapping("/list")
	public Mono<TableDataInfo> list(ServerWebExchange exchange, SysDictType dictType) {
		return super.list(exchange, dictType);
	}

	@Log(title = "字典类型", businessType = BusinessType.EXPORT)
	@PreAuthorize("@ss.hasPermi('system:dict:export')")
	@PostMapping("/export")
	public Mono<Void> export(ServerHttpResponse response, SysDictType dictType) {
		return super.export(response, dictType, SysDictType.class, "字典类型");
	}

	/**
	 * 查询字典类型详细
	 */
	@PreAuthorize("@ss.hasPermi('system:dict:query')")
	@GetMapping(value = "/{dictId}")
	public Mono<AjaxResult> getInfo(@PathVariable Long dictId) {
		return super.getInfo(dictId);
	}

	/**
	 * 新增字典类型
	 */
	@PreAuthorize("@ss.hasPermi('system:dict:add')")
	@Log(title = "字典类型", businessType = BusinessType.INSERT)
	@PostMapping
	public Mono<AjaxResult> add(@Validated @RequestBody SysDictType dict) {
		return startLoginUserMono(loginUser -> {
			return service.checkDictTypeUnique(dict).flatMap(unique -> {
				if (UserConstants.NOT_UNIQUE.equals(unique)) {
					return AjaxResult.errorMono("新增字典'" + dict.getDictName() + "'失败，字典类型已存在");
				}
				dict.setCreateBy(loginUser.getUsername());
				return service.insert(dict).map(this::toAjax);
			});
		});
	}

	/**
	 * 修改字典类型
	 */
	@PreAuthorize("@ss.hasPermi('system:dict:edit')")
	@Log(title = "字典类型", businessType = BusinessType.UPDATE)
	@PutMapping
	public Mono<AjaxResult> edit(@Validated @RequestBody SysDictType dict) {
		return startLoginUserMono(loginUser -> {
			return service.checkDictTypeUnique(dict).flatMap(unique -> {
				if (UserConstants.NOT_UNIQUE.equals(unique)) {
					return AjaxResult.errorMono("修改字典'" + dict.getDictName() + "'失败，字典类型已存在");
				}
				dict.setUpdateBy(loginUser.getUsername());
				return service.update(dict).map(this::toAjax);
			});
		});
	}

	/**
	 * 删除字典类型
	 */
	@PreAuthorize("@ss.hasPermi('system:dict:remove')")
	@Log(title = "字典类型", businessType = BusinessType.DELETE)
	@DeleteMapping("/{dictIds}")
	public Mono<AjaxResult> remove(@PathVariable Long[] dictIds) {
		return startMono(() -> {
			return service.deleteByIds(dictIds).thenReturn(success());
		});
	}

	/**
	 * 刷新字典缓存
	 */
	@PreAuthorize("@ss.hasPermi('system:dict:remove')")
	@Log(title = "字典类型", businessType = BusinessType.CLEAN)
	@DeleteMapping("/refreshCache")
	public Mono<AjaxResult> refreshCache() {
		return startMono(() -> {
			return service.resetDictCache().thenReturn(AjaxResult.success());
		});
	}

	/**
	 * 获取字典选择框列表
	 */
	@GetMapping("/optionselect")
	public Mono<AjaxResult> optionselect() {
		return startMono(() -> {
			return service.selectDictTypeAll().map(AjaxResult::success);
		});
	}
}
