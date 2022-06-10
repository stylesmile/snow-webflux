package com.snow.framework.web.exception;

import com.alibaba.fastjson.JSON;
import com.snow.common.exception.base.BaseException;
import com.snow.common.utils.WebServerUtils;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.snow.common.core.domain.AjaxResult;
import com.snow.common.exception.DemoModeException;
import com.snow.common.exception.ServiceException;
import com.snow.common.utils.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/**
 * 全局异常处理器
 *
 * @author ruoyi
 */
@Configuration
@Order(-2)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	private static final int BizCode = HttpStatus.INTERNAL_SERVER_ERROR.value();

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
		AjaxResult result = null;
		if (ex instanceof BaseException) {
			result = handleException((BaseException) ex, exchange.getRequest());
		} else if (ex instanceof ServiceException) {
			result = handleServiceException((ServiceException) ex, exchange.getRequest());
		} else if (ex instanceof HttpRequestMethodNotSupportedException) {
			result = handleHttpRequestMethodNotSupportedException((HttpRequestMethodNotSupportedException) ex, exchange.getRequest());
		} else if (ex instanceof UnknownHostException) {
			result = handleException((UnknownHostException) ex, exchange.getRequest());
		} else if (ex instanceof ConnectException) {
			result = handleException((ConnectException) ex, exchange.getRequest());
		} else if (ex instanceof AccessDeniedException) {
			result = handleAccessDeniedException((AccessDeniedException) ex, exchange.getRequest());
		} else if (ex instanceof AccountExpiredException) {
			result = handleException((AccountExpiredException) ex, exchange.getRequest());
		} else if (ex instanceof UsernameNotFoundException) {
			result = handleException((UsernameNotFoundException) ex, exchange.getRequest());
		} else if (ex instanceof BindException) {
			result = handleBindException((BindException) ex, exchange.getRequest());
		} else if (ex instanceof MethodArgumentNotValidException) {
			result = handleMethodArgumentNotValidException((MethodArgumentNotValidException) ex, exchange.getRequest());
		} else if (ex instanceof DemoModeException) {
			result = handleDemoModeException((DemoModeException) ex, exchange.getRequest());
		} else {
			result = handleException(ex, exchange.getRequest());
		}

		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(HttpStatus.OK);
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
		DataBufferFactory bufferFactory = response.bufferFactory();
		DataBuffer dataBuffer = bufferFactory.wrap(JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8));
		return response.writeWith(Mono.just(dataBuffer));
	}

	/**
	 * 权限校验异常
	 */
	private AjaxResult handleAccessDeniedException(AccessDeniedException e, ServerHttpRequest request) {
		String requestURI = WebServerUtils.getURI(request);
		log.error("请求地址'{}',权限校验失败'{}'", requestURI, e.getMessage());
		return AjaxResult.error(HttpStatus.FORBIDDEN.value(), "没有权限，请联系管理员授权");
	}

	/**
	 * 请求方式不支持
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	private AjaxResult handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e,
																	ServerHttpRequest request) {
		String requestURI = WebServerUtils.getURI(request);
		log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
		return AjaxResult.error(BizCode, e.getMessage());
	}

	/**
	 * 业务异常
	 */
	@ExceptionHandler(ServiceException.class)
	private AjaxResult handleServiceException(ServiceException e, ServerHttpRequest request) {
		log.error(e.getMessage(), e);
		Integer code = e.getCode();
		return StringUtils.isNotNull(code)
			? AjaxResult.error(code, e.getMessage())
			: AjaxResult.error(BizCode, e.getMessage());
	}

	/**
	 * 系统异常
	 */
	@ExceptionHandler(Exception.class)
	private AjaxResult handleException(Throwable e, ServerHttpRequest request) {
		String requestURI = WebServerUtils.getURI(request);
		log.error("请求地址'{}',发生系统异常.", requestURI, e);
		return AjaxResult.error(BizCode, e.getMessage());
	}

	/**
	 * 自定义验证异常
	 */
	@ExceptionHandler(BindException.class)
	private AjaxResult handleBindException(BindException e, ServerHttpRequest request) {
		log.error(e.getMessage(), e);
		String message = e.getAllErrors().get(0).getDefaultMessage();
		return AjaxResult.error(BizCode, message);
	}

	/**
	 * 自定义验证异常
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	private AjaxResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e, ServerHttpRequest request) {
		log.error(e.getMessage(), e);
		String message = e.getBindingResult().getFieldError().getDefaultMessage();
		return AjaxResult.error(BizCode, message);
	}

	/**
	 * 演示模式异常
	 */
	@ExceptionHandler(DemoModeException.class)
	private AjaxResult handleDemoModeException(DemoModeException e, ServerHttpRequest request) {
		return AjaxResult.error(BizCode, "演示模式，不允许操作");
	}
}
