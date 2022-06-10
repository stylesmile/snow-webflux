package com.snow.common.utils;

import com.snow.common.constant.HttpStatusCode;
import com.snow.common.core.domain.model.LoginUser;
import com.snow.common.exception.ServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reactor.core.publisher.Mono;

/**
 * 安全服务工具类
 *
 * @author ruoyi
 */
public class SecurityUtils {
	/**
	 * 用户ID
	 **/
	public static Mono<Long> getUserId() {
		return getLoginUser().map(user -> user.getUserId());
	}

//    /**
//     * 获取部门ID
//     **/
//    public static Long getDeptId()
//    {
//        try
//        {
//            return getLoginUser().getDeptId();
//        }
//        catch (Exception e)
//        {
//            throw new ServiceException("获取部门ID异常", HttpStatus.UNAUTHORIZED);
//        }
//    }
//
//    /**
//     * 获取用户账户
//     **/
//    public static String getUsername()
//    {
//        try
//        {
//            return getLoginUser().getUsername();
//        }
//        catch (Exception e)
//        {
//            throw new ServiceException("获取用户账户异常", HttpStatus.UNAUTHORIZED);
//        }
//    }

	/**
	 * 获取用户
	 **/
	public static LoginUser getLoginUserSync() {
		try {
			return (LoginUser) getAuthenticationSync().getPrincipal();
		} catch (Exception e) {
			throw new ServiceException("获取用户信息异常", HttpStatusCode.UNAUTHORIZED);
		}
	}

	/**
	 * 获取Authentication
	 */
	private static Authentication getAuthenticationSync() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	/**
	 * 获取当前登录用户
	 **/
	public static Mono<LoginUser> getLoginUser() {
		return ReactiveSecurityContextHolder.
			getContext().
			map(context -> ((LoginUser) context.getAuthentication().getPrincipal()));
	}

	/**
	 * 生成BCryptPasswordEncoder密码
	 *
	 * @param password 密码
	 * @return 加密字符串
	 */
	public static String encryptPassword(String password) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return passwordEncoder.encode(password);
	}

	/**
	 * 判断密码是否相同
	 *
	 * @param rawPassword     真实密码
	 * @param encodedPassword 加密后字符
	 * @return 结果
	 */
	public static boolean matchesPassword(String rawPassword, String encodedPassword) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}

	/**
	 * 是否为管理员
	 *
	 * @param userId 用户ID
	 * @return 结果
	 */
	public static boolean isAdmin(Long userId) {
		return userId != null && 1L == userId;
	}
}
