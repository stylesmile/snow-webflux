package com.snow.web.controller.system;

import com.snow.common.annotation.Log;
import com.snow.common.config.RuoYiConfig;
import com.snow.common.constant.UserConstants;
import com.snow.common.core.controller.BaseController;
import com.snow.common.core.domain.AjaxResult;
import com.snow.common.core.domain.entity.SysUser;
import com.snow.common.enums.BusinessType;
import com.snow.common.exception.ServiceException;
import com.snow.common.utils.SecurityUtils;
import com.snow.common.utils.StringUtils;
import com.snow.common.utils.file.FileUploadUtils;
import com.snow.framework.web.service.TokenService;
import com.snow.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * 个人信息 业务处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/user/profile")
public class SysProfileController extends BaseController {
	@Autowired
	private ISysUserService userService;

	@Autowired
	private TokenService tokenService;

	/**
	 * 个人信息
	 */
	@GetMapping
	public Mono<AjaxResult> profile() {
		return startLoginUserMono(loginUser -> {
			SysUser user = loginUser.getUser();
			return userService.selectUserRoleGroup(loginUser.getUsername()).flatMap(roleGroup -> {
				return userService.selectUserPostGroup(loginUser.getUsername()).map(postGroup -> {
					return AjaxResult.success(user).put("roleGroup", roleGroup)
						.put("postGroup", postGroup);
				});
			});
		});
	}

	/**
	 * 修改用户
	 */
	@Log(title = "个人信息", businessType = BusinessType.UPDATE)
	@PutMapping
	public Mono<AjaxResult> updateProfile(@RequestBody SysUser user) {
		return startLoginUserMono(loginUser -> {
			SysUser sysUser = loginUser.getUser();
			user.setUserName(sysUser.getUserName());
			return userService.checkPhoneUnique(user).flatMap(unique -> {
				if (StringUtils.isNotEmpty(user.getPhonenumber())
					&& UserConstants.NOT_UNIQUE.equals(unique)) {
					throw new ServiceException("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
				}
				return userService.checkEmailUnique(user);
			}).flatMap(unique -> {
				if (StringUtils.isNotEmpty(user.getEmail())
					&& UserConstants.NOT_UNIQUE.equals(unique)) {
					throw new ServiceException("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
				}
				user.setUserId(sysUser.getUserId());
				user.setPassword(null);
				return userService.updateUserProfile(user).map(rows -> {
					if (rows > 0) {
						// 更新缓存用户信息
						sysUser.setNickName(user.getNickName());
						sysUser.setPhonenumber(user.getPhonenumber());
						sysUser.setEmail(user.getEmail());
						sysUser.setSex(user.getSex());
						tokenService.setLoginUser(loginUser);
						return AjaxResult.success();
					}
					return AjaxResult.error("修改个人信息异常，请联系管理员");
				});
			});
		});
	}

	/**
	 * 重置密码
	 */
	@Log(title = "个人信息", businessType = BusinessType.UPDATE)
	@PutMapping("/updatePwd")
	public Mono<AjaxResult> updatePwd(String oldPassword, String newPassword) {
		return startLoginUserMono(loginUser -> {
			String userName = loginUser.getUsername();
			String password = loginUser.getPassword();
			if (!SecurityUtils.matchesPassword(oldPassword, password)) {
				return AjaxResult.errorMono("修改密码失败，旧密码错误");
			}
			if (SecurityUtils.matchesPassword(newPassword, password)) {
				return AjaxResult.errorMono("新密码不能与旧密码相同");
			}
			return userService.resetUserPwd(userName, SecurityUtils.encryptPassword(newPassword)).map(rows -> {
				if (rows > 0) {
					// 更新缓存用户密码
					loginUser.getUser().setPassword(SecurityUtils.encryptPassword(newPassword));
					tokenService.setLoginUser(loginUser);
					return AjaxResult.success();
				}
				return AjaxResult.error("修改密码异常，请联系管理员");
			});
		});
	}

	/**
	 * 头像上传
	 */
	@Log(title = "用户头像", businessType = BusinessType.UPDATE)
	@PostMapping("/avatar")
	public Mono<AjaxResult> avatar(@RequestParam("avatarfile") MultipartFile file) {
		return startLoginUserMono(loginUser -> {
			if (!file.isEmpty()) {
				throw new ServiceException("上传图片异常，请联系管理员");
			}
			String avatar = null;
			try {
				avatar = FileUploadUtils.upload(RuoYiConfig.getAvatarPath(), file);
			} catch (IOException e) {
				return Mono.error(e);
			}
			return Mono.just(avatar).flatMap(avatar2 -> {
				return userService.updateUserAvatar(loginUser.getUsername(), avatar2).map(done -> {
					if (done) {
						AjaxResult ajax = AjaxResult.success();
						ajax.put("imgUrl", avatar2);
						// 更新缓存用户头像
						loginUser.getUser().setAvatar(avatar2);
						tokenService.setLoginUser(loginUser);
						return ajax;
					}
					return AjaxResult.error("上传图片异常，请联系管理员");
				});
			});
		});
	}
}
