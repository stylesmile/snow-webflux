package com.snow.web.controller.system;

import com.snow.common.annotation.Log;
import com.snow.common.biz.EntityController;
import com.snow.common.core.domain.AjaxResult;
import com.snow.common.core.domain.entity.SysDictData;
import com.snow.common.core.page.TableDataInfo;
import com.snow.common.enums.BusinessType;
import com.snow.web.service.ISysDictDataService;
import com.snow.web.service.ISysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/system/dict/data")
public class SysDictDataController extends EntityController<SysDictData, Long, ISysDictDataService> {

	@Autowired
	private ISysDictTypeService dictTypeService;

	@PreAuthorize("@ss.hasPermi('system:dict:list')")
	@GetMapping("/list")
	public Mono<TableDataInfo> list(ServerWebExchange exchange, SysDictData dictData) {
		return super.list(exchange, dictData);
	}

	@Log(title = "字典数据", businessType = BusinessType.EXPORT)
	@PreAuthorize("@ss.hasPermi('system:dict:export')")
	@PostMapping("/export")
	public Mono<Void> export(ServerHttpResponse response, SysDictData dictData) {
		return super.export(response, dictData, SysDictData.class, "字典数据");
	}

	/**
	 * 查询字典数据详细
	 */
	@PreAuthorize("@ss.hasPermi('system:dict:query')")
	@GetMapping(value = "/{dictCode}")
	public Mono<AjaxResult> getInfo(@PathVariable Long dictCode) {
		return super.getInfo(dictCode);
	}

	/**
	 * 根据字典类型查询字典数据信息
	 */
	@GetMapping(value = "/type/{dictType}")
	public Mono<AjaxResult> dictType(@PathVariable final String dictType) {
		return startMono(() -> {
			return dictTypeService.selectDictDataByType(dictType).map(AjaxResult::success);
		});
	}

	/**
	 * 新增字典类型
	 */
	@PreAuthorize("@ss.hasPermi('system:dict:add')")
	@Log(title = "字典数据", businessType = BusinessType.INSERT)
	@PostMapping
	public Mono<AjaxResult> add(@Validated @RequestBody SysDictData dict) {
		return startLoginUserMono(loginUser -> {
			dict.setCreateBy(loginUser.getUsername());
			return service.insert(dict).map(this::toAjax);
		});
	}

	/**
	 * 修改保存字典类型
	 */
	@PreAuthorize("@ss.hasPermi('system:dict:edit')")
	@Log(title = "字典数据", businessType = BusinessType.UPDATE)
	@PutMapping
	public Mono<AjaxResult> edit(@Validated @RequestBody SysDictData dict) {
		return startLoginUserMono(loginUser -> {
			dict.setUpdateBy(loginUser.getUsername());
			return service.update(dict).map(this::toAjax);
		});
	}

	/**
	 * 删除字典类型
	 */
	@PreAuthorize("@ss.hasPermi('system:dict:remove')")
	@Log(title = "字典类型", businessType = BusinessType.DELETE)
	@DeleteMapping("/{dictCodes}")
	public Mono<AjaxResult> remove(@PathVariable Long[] dictCodes) {
		return startMono(() -> {
			return service.deleteByIds(dictCodes).thenReturn(success());
		});
	}
}
