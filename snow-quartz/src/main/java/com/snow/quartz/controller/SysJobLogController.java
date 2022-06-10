package com.snow.quartz.controller;

import com.snow.common.annotation.Log;
import com.snow.common.biz.EntityController;
import com.snow.common.core.domain.AjaxResult;
import com.snow.common.core.page.TableDataInfo;
import com.snow.common.enums.BusinessType;
import com.snow.quartz.domain.SysJobLog;
import com.snow.quartz.service.ISysJobLogService;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 调度日志操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/jobLog")
public class SysJobLogController extends EntityController<SysJobLog, Long, ISysJobLogService> {
	/**
	 * 查询定时任务调度日志列表
	 */
	@PreAuthorize("@ss.hasPermi('monitor:job:list')")
	@GetMapping("/list")
	public Mono<TableDataInfo> list(ServerWebExchange exchange, SysJobLog where) {
		return super.list(exchange, where);
	}

	/**
	 * 导出定时任务调度日志列表
	 */
	@PreAuthorize("@ss.hasPermi('monitor:job:export')")
	@Log(title = "任务调度日志", businessType = BusinessType.EXPORT)
	@PostMapping("/export")
	public Mono<Void> export(ServerHttpResponse response, SysJobLog where) {
		return super.export(response, where, SysJobLog.class, "调度日志");

	}

	/**
	 * 根据调度编号获取详细信息
	 */
	@PreAuthorize("@ss.hasPermi('monitor:job:query')")
	@GetMapping(value = "/{configId}")
	public Mono<AjaxResult> getInfo(@PathVariable Long id) {
		return super.getInfo(id);
	}

	/**
	 * 删除定时任务调度日志
	 */
	@PreAuthorize("@ss.hasPermi('monitor:job:remove')")
	@Log(title = "定时任务调度日志", businessType = BusinessType.DELETE)
	@DeleteMapping("/{jobLogIds}")
	public Mono<AjaxResult> remove(@PathVariable Long[] ids) {
		return super.remove(ids);
	}

	/**
	 * 清空定时任务调度日志
	 */
	@PreAuthorize("@ss.hasPermi('monitor:job:remove')")
	@Log(title = "调度日志", businessType = BusinessType.CLEAN)
	@DeleteMapping("/clean")
	public Mono<AjaxResult> clean() {
		return startMono(() -> {
			return service.cleanJobLog().thenReturn(AjaxResult.success());
		});
	}
}
