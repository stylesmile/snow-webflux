package com.snow.web.controller.system;

import com.snow.common.annotation.Log;
import com.snow.common.biz.EntityController;
import com.snow.common.constant.UserConstants;
import com.snow.common.core.domain.AjaxResult;
import com.snow.common.core.page.TableDataInfo;
import com.snow.common.enums.BusinessType;
import com.snow.system.domain.SysConfig;
import com.snow.system.service.ISysConfigService;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 参数配置 信息操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/config")
public class SysConfigController extends EntityController<SysConfig, Integer, ISysConfigService> {
	/**
	 * 获取参数配置列表
	 */
	@Override
	@PreAuthorize("@ss.hasPermi('system:config:list')")
	@GetMapping("/list")
	public Mono<TableDataInfo> list(ServerWebExchange exchange, SysConfig config) {
		return super.list(exchange, config);
	}

	@Log(title = "参数管理", businessType = BusinessType.EXPORT)
	@PreAuthorize("@ss.hasPermi('system:config:export')")
	@PostMapping("/export")
	public Mono<Void> export(ServerHttpResponse response, SysConfig config) {
		return super.export(response, config, SysConfig.class, "参数数据");
	}

	/**
	 * 根据参数编号获取详细信息
	 */
	@Override
	@PreAuthorize("@ss.hasPermi('system:config:query')")
	@GetMapping(value = "/{configId}")
	public Mono<AjaxResult> getInfo(@PathVariable Integer configId) {
		return super.getInfo(configId);
	}

	/**
	 * 根据参数键名查询参数值
	 */
	@GetMapping(value = "/configKey/{configKey}")
	public Mono<AjaxResult> getConfigKey(@PathVariable String configKey) {
		return startMono(() -> {
			return service.selectConfigByKey(configKey).map(AjaxResult::success);
		});
	}

	/**
	 * 新增参数配置
	 */
	@PreAuthorize("@ss.hasPermi('system:config:add')")
	@Log(title = "参数管理", businessType = BusinessType.INSERT)
	@PostMapping
	public Mono<AjaxResult> add(@Validated @RequestBody SysConfig config) {
		return startLoginUserMono(loginUser -> {
			return service.checkConfigKeyUnique(config).flatMap(unique -> {
				if (UserConstants.NOT_UNIQUE.equals(unique)) {
					return AjaxResult.errorMono("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
				}
				config.setCreateBy(loginUser.getUsername());
				return service.insert(config).map(this::toAjax);
			});
		});
	}

	/**
	 * 修改参数配置
	 */
	@PreAuthorize("@ss.hasPermi('system:config:edit')")
	@Log(title = "参数管理", businessType = BusinessType.UPDATE)
	@PutMapping
	public Mono<AjaxResult> edit(@Validated @RequestBody SysConfig config) {
		return startLoginUserMono(loginUser -> {
			return service.checkConfigKeyUnique(config).flatMap(unique -> {
				if (UserConstants.NOT_UNIQUE.equals(unique)) {
					return AjaxResult.errorMono("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
				}
				config.setUpdateBy(loginUser.getUsername());
				return service.update(config).map(this::toAjax);
			});
		});
	}

	/**
	 * 删除参数配置
	 */
	@PreAuthorize("@ss.hasPermi('system:config:remove')")
	@Log(title = "参数管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{configIds}")
	public Mono<AjaxResult> remove(@PathVariable Integer[] configIds) {
		return super.remove(configIds);
	}

	/**
	 * 刷新参数缓存
	 */
	@PreAuthorize("@ss.hasPermi('system:config:remove')")
	@Log(title = "参数管理", businessType = BusinessType.CLEAN)
	@DeleteMapping("/refreshCache")
	public Mono<AjaxResult> refreshCache() {
		return startMono(() -> {
			return service.resetConfigCache().thenReturn(AjaxResult.success());
		});
	}
}
