package com.snow.web.controller.tool;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.snow.common.core.controller.BaseController;
import reactor.core.publisher.Mono;

/**
 * swagger 接口
 *
 * @author ruoyi
 */
@Controller
@RequestMapping("/tool/swagger")
public class SwaggerController extends BaseController {
	@PreAuthorize("@ss.hasPermi('tool:swagger:view')")
	@GetMapping()
	public Mono<String> index() {
		return startMono(() -> {
			return Mono.just(redirect("/swagger-ui.html"));
		});
	}
}
