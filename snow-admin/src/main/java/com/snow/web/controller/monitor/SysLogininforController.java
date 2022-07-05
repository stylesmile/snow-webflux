package com.snow.web.controller.monitor;

import com.snow.common.annotation.Log;
import com.snow.common.biz.EntityController;
import com.snow.common.core.domain.AjaxResult;
import com.snow.common.core.page.TableDataInfo;
import com.snow.common.enums.BusinessType;
import com.snow.web.domain.SysLoginInfor;
import com.snow.web.service.ISysLogininforService;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 系统访问记录
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/logininfor")
public class SysLogininforController extends EntityController<SysLoginInfor, Long, ISysLogininforService> {

	@PreAuthorize("@ss.hasPermi('monitor:logininfor:list')")
	@GetMapping("/list")
	public Mono<TableDataInfo> list(ServerWebExchange exchange, SysLoginInfor logininfor) {
		return super.list(exchange, logininfor);
	}

	@Log(title = "登录日志", businessType = BusinessType.EXPORT)
	@PreAuthorize("@ss.hasPermi('monitor:logininfor:export')")
	@PostMapping("/export")
	public Mono<Void> export(ServerHttpResponse response, SysLoginInfor logininfor) {
		return super.export(response, logininfor, SysLoginInfor.class, "登录日志");
	}

	@PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
	@Log(title = "登录日志", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	public Mono<AjaxResult> remove(@PathVariable Long[] ids) {
		return super.remove(ids);
	}

	@PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
	@Log(title = "登录日志", businessType = BusinessType.CLEAN)
	@DeleteMapping("/clean")
	public Mono<AjaxResult> clean() {
		return startMono(() -> {
			service.cleanLogininfor();
			return AjaxResult.successMono();
		});
	}
}
