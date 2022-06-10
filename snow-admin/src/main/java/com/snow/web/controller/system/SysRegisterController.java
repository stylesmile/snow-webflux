package com.snow.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.snow.common.core.controller.BaseController;
import com.snow.common.core.domain.AjaxResult;
import com.snow.common.core.domain.model.RegisterBody;
import com.snow.common.utils.StringUtils;
import com.snow.framework.web.service.SysRegisterService;
import com.snow.system.service.ISysConfigService;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 注册验证
 *
 * @author ruoyi
 */
@RestController
public class SysRegisterController extends BaseController {
	@Autowired
	private SysRegisterService registerService;

	@Autowired
	private ISysConfigService configService;

	@PostMapping("/register")
	public Mono<AjaxResult> register(ServerWebExchange exchange, @RequestBody RegisterBody user) {
		return startMono(() -> {
			if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser")))) {
				return errorMono("当前系统没有开启注册功能！");
			}
			return registerService.register(exchange, user).map(msg -> {
				return StringUtils.isEmpty(msg) ? success() : error(msg);
			});
		});

	}
}
