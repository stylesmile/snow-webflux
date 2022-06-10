package com.snow.web.controller.monitor;

import com.snow.common.core.controller.BaseController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.snow.common.core.domain.AjaxResult;
import com.snow.framework.web.domain.Server;
import reactor.core.publisher.Mono;

/**
 * 服务器监控
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/server")
public class ServerController extends BaseController {
	@PreAuthorize("@ss.hasPermi('monitor:server:list')")
	@GetMapping()
	public Mono<AjaxResult> getInfo() {
		return startMono(() -> {

			Server server = new Server();
			try {
				server.copyTo();
			} catch (Exception e) {
				return Mono.error(e);
			}
			return AjaxResult.successMono(server);
		});
	}
}
