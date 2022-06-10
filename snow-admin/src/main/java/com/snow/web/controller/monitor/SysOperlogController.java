package com.snow.web.controller.monitor;

import com.snow.common.annotation.Log;
import com.snow.common.biz.EntityController;
import com.snow.common.core.domain.AjaxResult;
import com.snow.common.core.page.TableDataInfo;
import com.snow.common.enums.BusinessType;
import com.snow.system.domain.SysOperLog;
import com.snow.system.service.ISysOperLogService;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 操作日志记录
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/operlog")
public class SysOperlogController extends EntityController<SysOperLog, Long, ISysOperLogService> {

	@PreAuthorize("@ss.hasPermi('monitor:operlog:list')")
	@GetMapping("/list")
	public Mono<TableDataInfo> list(ServerWebExchange exchange, SysOperLog operLog) {
		return super.list(exchange, operLog);
	}

	@Log(title = "操作日志", businessType = BusinessType.EXPORT)
	@PreAuthorize("@ss.hasPermi('monitor:operlog:export')")
	@PostMapping("/export")
	public Mono<Void> export(ServerHttpResponse response, SysOperLog operLog) {
		return super.export(response, operLog, SysOperLog.class, "操作日志");
	}

	@Log(title = "操作日志", businessType = BusinessType.DELETE)
	@PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
	@DeleteMapping("/{ids}")
	public Mono<AjaxResult> remove(@PathVariable Long[] ids) {
		return super.remove(ids);
	}

	@Log(title = "操作日志", businessType = BusinessType.CLEAN)
	@PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
	@DeleteMapping("/clean")
	public Mono<AjaxResult> clean() {
		return startMono(() -> {
			service.cleanOperLog();
			return AjaxResult.successMono();
		});
	}
}
