package com.snow.web.controller.monitor;

import com.snow.common.annotation.Log;
import com.snow.common.constant.Constants;
import com.snow.common.core.controller.BaseController;
import com.snow.common.core.domain.AjaxResult;
import com.snow.common.core.domain.model.LoginUser;
import com.snow.common.core.page.TableDataInfo;
import com.snow.common.core.redis.RedisCache;
import com.snow.common.enums.BusinessType;
import com.snow.common.utils.StringUtils;
import com.snow.web.domain.SysUserOnline;
import com.snow.web.service.ISysUserOnlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 在线用户监控
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/online")
public class SysUserOnlineController extends BaseController {
	@Autowired
	private ISysUserOnlineService userOnlineService;

	@Autowired
	private RedisCache redisCache;

	@PreAuthorize("@ss.hasPermi('monitor:online:list')")
	@GetMapping("/list")
	public Mono<TableDataInfo> list(String ipaddr, String userName) {
		return startMono(() -> {
			Collection<String> keys = redisCache.keys(Constants.LOGIN_TOKEN_KEY + "*");
			List<SysUserOnline> userOnlineList = new ArrayList<>();
			for (String key : keys) {
				LoginUser user = redisCache.getCacheObject(key);
				if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName)) {
					if (StringUtils.equals(ipaddr, user.getIpaddr()) && StringUtils.equals(userName, user.getUsername())) {
						userOnlineList.add(userOnlineService.selectOnlineByInfo(ipaddr, userName, user));
					}
				} else if (StringUtils.isNotEmpty(ipaddr)) {
					if (StringUtils.equals(ipaddr, user.getIpaddr())) {
						userOnlineList.add(userOnlineService.selectOnlineByIpaddr(ipaddr, user));
					}
				} else if (StringUtils.isNotEmpty(userName) && StringUtils.isNotNull(user.getUser())) {
					if (StringUtils.equals(userName, user.getUsername())) {
						userOnlineList.add(userOnlineService.selectOnlineByUserName(userName, user));
					}
				} else {
					userOnlineList.add(userOnlineService.loginUserToUserOnline(user));
				}
			}
			Collections.reverse(userOnlineList);
			userOnlineList.removeAll(Collections.singleton(null));
			return Mono.just(getDataTable(userOnlineList, userOnlineList.size()));
		});
	}

	/**
	 * 强退用户
	 */
	@PreAuthorize("@ss.hasPermi('monitor:online:forceLogout')")
	@Log(title = "在线用户", businessType = BusinessType.FORCE)
	@DeleteMapping("/{tokenId}")
	public Mono<AjaxResult> forceLogout(@PathVariable String tokenId) {
		return startMono(() -> {

			redisCache.deleteObject(Constants.LOGIN_TOKEN_KEY + tokenId);
			return AjaxResult.successMono();
		});
	}
}
