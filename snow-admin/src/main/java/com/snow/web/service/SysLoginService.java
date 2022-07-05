package com.snow.web.service;

import com.snow.common.constant.Constants;
import com.snow.common.core.domain.entity.SysUser;
import com.snow.common.core.domain.model.LoginUser;
import com.snow.common.core.redis.RedisCache;
import com.snow.common.exception.ServiceException;
import com.snow.common.exception.user.CaptchaException;
import com.snow.common.exception.user.CaptchaExpireException;
import com.snow.common.exception.user.UserPasswordNotMatchException;
import com.snow.common.utils.LocalDateTimeUtils;
import com.snow.common.utils.MessageUtils;
import com.snow.common.utils.StringUtils;
import com.snow.common.utils.ip.IpUtils;
import com.snow.framework.manager.AsyncManager;
import com.snow.web.com.config.security.AsyncFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * 登录校验方法
 *
 * @author ruoyi
 */
@Component
public class SysLoginService {
	@Autowired
	private TokenService tokenService;

	@Resource
	private ReactiveAuthenticationManager authenticationManager;

	@Autowired
	private RedisCache redisCache;

	@Autowired
	private ISysUserService userService;

	@Autowired
	private ISysConfigService configService;

	/**
	 * 登录验证
	 *
	 * @param username 用户名
	 * @param password 密码
	 * @param code     验证码
	 * @param uuid     唯一标识
	 * @return 结果
	 */
	public Mono<String> login(ServerWebExchange exchange, final String username,
							  final String password,
							  final String code,
							  final String uuid) {
		return configService.selectCaptchaOnOff().map(captchaOnOff -> {
			// 验证码开关
			if (captchaOnOff) {
				validateCaptcha(exchange, username, code, uuid);
			}
			return captchaOnOff;
		}).flatMap(captchaOnOff -> {
			// 用户验证
			// 该方法会去调用 UserDetailsServiceImpl.findByUsername
			return authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, password))
				.map(authentication -> {
					AsyncManager.me().execute(
						AsyncFactory.recordLogininfor(
							exchange.getRequest(),
							username,
							Constants.LOGIN_SUCCESS,
							MessageUtils.message("user.login.success")));
					LoginUser loginUser = (LoginUser) authentication.getPrincipal();
					recordLoginInfo(exchange.getRequest(), loginUser.getUserId());
					// 生成token
					return tokenService.createToken(exchange.getRequest(), loginUser);
				}).doOnError(e -> {
					if (e instanceof BadCredentialsException) {
						AsyncManager.me().execute(
							AsyncFactory.recordLogininfor(
								exchange.getRequest(),
								username,
								Constants.LOGIN_FAIL,
								MessageUtils.message("user.password.not.match")));
						throw new UserPasswordNotMatchException();
					} else {
						AsyncManager.me().execute(
							AsyncFactory.recordLogininfor(
								exchange.getRequest(),
								username,
								Constants.LOGIN_FAIL,
								e.getMessage()));
						throw new ServiceException(e.getMessage());
					}
				});
		});

	}

	/**
	 * 校验验证码
	 *
	 * @param username 用户名
	 * @param code     验证码
	 * @param uuid     唯一标识
	 * @return 结果
	 */
	public void validateCaptcha(ServerWebExchange exchange, final String username, final String code, final String uuid) {
		String verifyKey = Constants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
		String captcha = redisCache.getCacheObject(verifyKey);
		redisCache.deleteObject(verifyKey);
		if (captcha == null) {
			AsyncManager.me().execute(AsyncFactory.recordLogininfor(exchange.getRequest(),
				username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire")));
			throw new CaptchaExpireException();
		}
		if (!code.equalsIgnoreCase(captcha)) {
			AsyncManager.me().execute(AsyncFactory.recordLogininfor(exchange.getRequest(),
				username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
			throw new CaptchaException();
		}
	}

	/**
	 * 记录登录信息
	 *
	 * @param userId 用户ID
	 */
	public void recordLoginInfo(ServerHttpRequest request, Long userId) {
		SysUser sysUser = new SysUser();
		sysUser.setUserId(userId);
		sysUser.setLoginIp(IpUtils.getIpAddr(request));
		sysUser.setLoginDate(LocalDateTimeUtils.getNowDate());
		userService.updateUserProfile(sysUser);
	}
}
