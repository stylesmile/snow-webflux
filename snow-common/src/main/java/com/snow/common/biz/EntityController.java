package com.snow.common.biz;

import com.snow.common.core.controller.BaseController;
import com.snow.common.core.domain.AjaxResult;
import com.snow.common.core.domain.BaseEntity;
import com.snow.common.core.page.TableDataInfo;
import com.snow.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public abstract class EntityController<E extends BaseEntity, K, S extends IService<E, K>> extends BaseController {

	@Autowired
	protected S service;

	/**
	 * 查询数据列表
	 */
	public Mono<TableDataInfo> list(ServerWebExchange exchange, E cond) {
		return startMono(() -> {
			startPage(exchange.getRequest(), cond);
			return service.selectList(cond).flatMap(list -> {
				if (cond.isPaged()) {
					return service.countList(cond).map(total -> {
						return getDataTable(list, total);
					});
				}
				return Mono.just(getDataTable(list, list.size()));
			});
		});
	}

	/**
	 * 导出数据列表
	 */
	public Mono<Void> export(ServerHttpResponse response, E where, Class<E> clazz, final String sheetName) {
		return startVoid(() -> {
			return service.selectList(where).flatMap(list -> {
				ExcelUtil<E> util = new ExcelUtil<>(clazz);
				return util.exportExcel(response, list, sheetName);
			});
		});
	}

	/**
	 * 根据ID获取详细信息
	 */
	public Mono<AjaxResult> getInfo(K id) {
		return startMono(() -> {
			return service.selectById(id).map(entity -> {
				return AjaxResult.success(entity);
			});
		});
	}

	/**
	 * 修改
	 */
	public Mono<AjaxResult> edit(E entity) {
		return startMono(() -> {
			return service.update(entity).map(this::toAjax);
		});
	}

	/**
	 * 删除菜单
	 */
	public Mono<AjaxResult> remove(K id) {
		return startMono(() -> {
			return service.deleteById(id).map(this::toAjax);
		});
	}

	/**
	 * 删除数据
	 */
	public Mono<AjaxResult> remove(@PathVariable K[] ids) {
		return startMono(() -> {
			return service.deleteByIds(ids).map(this::toAjax);
		});
	}
}
