package com.snow.web.service;

import com.snow.common.core.domain.entity.SysUser;
import com.snow.common.core.domain.model.LoginUser;
import com.snow.common.enums.UserStatus;
import com.snow.common.exception.ServiceException;
import com.snow.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 用户验证处理
 *
 * @author ruoyi
 */
@Service
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {
	private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

	@Autowired
	private ISysUserService userService;

	@Autowired
	private SysPermissionService permissionService;

	/**
	 * 返回用户的信息，Spring 会校验 password
	 */
	@Override
	public Mono<UserDetails> findByUsername(String username) {
		return userService.selectUserByUserName(username).doOnSuccess(user -> {
			if (StringUtils.isNull(user)) {
				log.info("登录用户：{} 不存在.", username);
				throw new ServiceException("登录用户：" + username + " 不存在");
			}
		}).flatMap(user -> {
			if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
				log.info("登录用户：{} 已被删除.", username);
				throw new ServiceException("对不起，您的账号：" + username + " 已被删除");
			} else if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
				log.info("登录用户：{} 已被停用.", username);
				throw new ServiceException("对不起，您的账号：" + username + " 已停用");
			}
			return createLoginUser(user);
		});
	}

	public Mono<UserDetails> createLoginUser(SysUser user) {
		return permissionService.getMenuPermission(user).map(perms -> {
			return new LoginUser(user.getUserId(), user.getDeptId(), user, perms);
		});

	}
}
