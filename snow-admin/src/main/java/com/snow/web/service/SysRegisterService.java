package com.snow.web.service;

import com.snow.web.com.config.security.AsyncFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.snow.common.constant.Constants;
import com.snow.common.constant.UserConstants;
import com.snow.common.core.domain.entity.SysUser;
import com.snow.common.core.domain.model.RegisterBody;
import com.snow.common.core.redis.RedisCache;
import com.snow.common.exception.user.CaptchaException;
import com.snow.common.exception.user.CaptchaExpireException;
import com.snow.common.utils.MessageUtils;
import com.snow.common.utils.SecurityUtils;
import com.snow.common.utils.StringUtils;
import com.snow.framework.manager.AsyncManager;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 注册校验方法
 *
 * @author ruoyi
 */
@Component
public class SysRegisterService {
	@Autowired
	private ISysUserService userService;

	@Autowired
	private ISysConfigService configService;

	@Autowired
	private RedisCache redisCache;

	/**
	 * 注册
	 */
	public Mono<String> register(ServerWebExchange exchange, RegisterBody registerBody) {

		return configService.selectCaptchaOnOff().flatMap(captchaOnOff -> {
			String msg = "";
			String username = registerBody.getUsername();
			String password = registerBody.getPassword();

			// 验证码开关
			if (captchaOnOff) {
				validateCaptcha(username, registerBody.getCode(), registerBody.getUuid());
			}

			if (StringUtils.isEmpty(username)) {
				msg = "用户名不能为空";
			} else if (StringUtils.isEmpty(password)) {
				msg = "用户密码不能为空";
			} else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
				|| username.length() > UserConstants.USERNAME_MAX_LENGTH) {
				msg = "账户长度必须在2到20个字符之间";
			} else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
				|| password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
				msg = "密码长度必须在5到20个字符之间";
			} else {
				return userService.checkUserNameUnique(username).flatMap(result -> {
					if (UserConstants.NOT_UNIQUE.equals(result)) {
						return Mono.just("保存用户'" + username + "'失败，注册账号已存在");
					}
					SysUser sysUser = new SysUser();
					sysUser.setUserName(username);
					sysUser.setNickName(username);
					sysUser.setPassword(SecurityUtils.encryptPassword(registerBody.getPassword()));
					return userService.registerUser(sysUser).map(regFlag -> {
						if (!regFlag) {
							return "注册失败,请联系系统管理人员";
						} else {
							AsyncManager.me().execute(AsyncFactory.recordLogininfor(exchange.getRequest(),
								username, Constants.REGISTER,
								MessageUtils.message("user.register.success")));
						}
						return "";
					});
				});
			}
			return Mono.just(msg);
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
	public void validateCaptcha(String username, String code, String uuid) {
		String verifyKey = Constants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
		String captcha = redisCache.getCacheObject(verifyKey);
		redisCache.deleteObject(verifyKey);
		if (captcha == null) {
			throw new CaptchaExpireException();
		}
		if (!code.equalsIgnoreCase(captcha)) {
			throw new CaptchaException();
		}
	}
}
