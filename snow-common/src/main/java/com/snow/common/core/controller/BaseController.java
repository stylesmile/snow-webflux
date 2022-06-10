package com.snow.common.core.controller;

import com.snow.common.core.domain.AjaxResult;
import com.snow.common.core.domain.PagingParam;
import com.snow.common.core.domain.model.LoginUser;
import com.snow.common.core.page.TableDataInfo;
import com.snow.common.utils.DateUtils;
import com.snow.common.utils.PageUtils;
import com.snow.common.utils.SecurityUtils;
import com.snow.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import reactor.core.publisher.Mono;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * web层通用数据处理
 *
 * @author ruoyi
 */
public class BaseController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 将前台传递过来的日期格式的字符串，自动转化为Date类型
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		// Date 类型转换
		binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				setValue(DateUtils.parseDate(text));
			}
		});
	}

	/**
	 * 启动反应式函数，返回
	 */
	protected <T> Mono<T> startMono(Supplier<? extends Mono<? extends T>> supplier) {
		return Mono.defer(supplier);
	}

	/**
	 * 启动反应式函数，返回
	 */
	protected <T> Mono<T> startLoginUserMono(Function<? super LoginUser, ? extends Mono<? extends T>> supplier) {
		return Mono.defer(() -> getLoginUser()).flatMap(loginUser -> supplier.apply(loginUser));
	}

	/**
	 * 启动反应式，返回Void
	 */
	protected <T> Mono<Void> startVoid(Supplier<? extends Mono<? extends T>> supplier) {
		return Mono.defer(supplier).then();
	}

	/**
	 * 设置请求分页数据
	 */
	protected void startPage(ServerHttpRequest request, PagingParam pagingParam) {
		PageUtils.startPage(request, pagingParam);
	}

	/**
	 * 响应请求分页数据
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	protected TableDataInfo getDataTable(List<?> list, final long total) {
		return PageUtils.getDataTable(list, total);
	}

	/**
	 * 返回成功
	 */
	public AjaxResult success() {
		return AjaxResult.success();
	}

	/**
	 * 返回成功
	 */
	public Mono<AjaxResult> successMono() {
		return Mono.just(success());
	}

	/**
	 * 返回失败消息
	 */
	public AjaxResult error() {
		return AjaxResult.error();
	}

	/**
	 * 返回成功消息
	 */
	public AjaxResult success(String message) {
		return AjaxResult.success(message);
	}

	/**
	 * 返回失败消息
	 */
	public AjaxResult error(String message) {
		return AjaxResult.error(message);
	}

	/**
	 * 返回失败消息
	 */
	public Mono<AjaxResult> errorMono(String message) {
		return Mono.just(error(message));
	}

	/**
	 * 响应返回结果
	 *
	 * @param rows 影响行数
	 * @return 操作结果
	 */
	protected AjaxResult toAjax(final long rows) {
		return rows > 0 ? AjaxResult.success() : AjaxResult.error();
	}

	/**
	 * 响应返回结果
	 *
	 * @param rows 影响行数
	 * @return 操作结果
	 */
	protected Mono<AjaxResult> toAjaxMono(int rows) {
		return Mono.just(toAjax(rows));
	}

	/**
	 * 响应返回结果
	 *
	 * @param result 结果
	 * @return 操作结果
	 */
	protected AjaxResult toAjax(boolean result) {
		return result ? success() : error();
	}

	/**
	 * 响应返回结果
	 *
	 * @param result 结果
	 * @return 操作结果
	 */
	protected Mono<AjaxResult> toAjaxMono(boolean result) {
		return Mono.just(toAjax(result));
	}

	/**
	 * 页面跳转
	 */
	public String redirect(String url) {
		return StringUtils.format("redirect:{}", url);
	}

	/**
	 * 获取用户缓存信息
	 */
	public Mono<LoginUser> getLoginUser() {
		return SecurityUtils.getLoginUser();
	}

}
