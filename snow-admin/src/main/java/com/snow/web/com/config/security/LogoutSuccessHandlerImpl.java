package com.snow.web.com.config.security;

import com.alibaba.fastjson.JSON;
import com.snow.common.constant.Constants;
import com.snow.common.constant.HttpStatusCode;
import com.snow.common.core.domain.AjaxResult;
import com.snow.common.core.domain.model.LoginUser;
import com.snow.common.utils.StringUtils;
import com.snow.common.utils.WebServerUtils;
import com.snow.framework.manager.AsyncManager;
import com.snow.web.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import reactor.core.publisher.Mono;

/**
 * 自定义退出处理类 返回成功
 *
 * @author ruoyi
 */
@Configuration
public class LogoutSuccessHandlerImpl implements ServerLogoutSuccessHandler {
	@Autowired
	private TokenService tokenService;

	/**
	 * Invoked after log out was successful
	 *
	 * @param exchange       the exchange
	 * @param authentication the {@link Authentication}
	 * @return a completion notification (success or error)
	 */
	@Override
	public Mono<Void> onLogoutSuccess(WebFilterExchange exchange, Authentication authentication) {
		ServerHttpRequest request = exchange.getExchange().getRequest();
		ServerHttpResponse response = exchange.getExchange().getResponse();

		LoginUser loginUser = tokenService.getLoginUser(request);
		if (StringUtils.isNotNull(loginUser)) {
			String userName = loginUser.getUsername();
			// 删除用户缓存记录
			tokenService.delLoginUser(loginUser.getToken());
			// 记录用户退出日志
			AsyncManager.me().execute(AsyncFactory.recordLogininfor(request, userName, Constants.LOGOUT, "退出成功"));
		}
		return WebServerUtils.renderString(response, JSON.toJSONString(AjaxResult.error(HttpStatusCode.SUCCESS, "退出成功")));
	}
}
