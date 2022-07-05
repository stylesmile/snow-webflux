package com.snow.web.com.config.security;

import com.alibaba.fastjson.JSON;
import com.snow.common.constant.HttpStatusCode;
import com.snow.common.core.domain.AjaxResult;
import com.snow.common.utils.WebServerUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;

/**
 * 认证失败处理类 返回未授权
 *
 * @author ruoyi
 */
@Component
public class ServerAuthenticationEntryPointImpl implements ServerAuthenticationEntryPoint {

	/**
	 * 对于未授权的访问，返回未授权信息
	 */
	@Override
	public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
		String msg = MessageFormat.format("请求访问：{0}，认证失败，无法访问系统资源",
			exchange.getRequest().getPath().value());
		return WebServerUtils.renderString(exchange.getResponse(),
			JSON.toJSONString(AjaxResult.create(HttpStatusCode.UNAUTHORIZED, msg)));
	}
}

