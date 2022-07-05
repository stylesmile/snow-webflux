package com.snow.web.service;

import com.snow.common.biz.IService;
import com.snow.web.domain.SysLoginInfor;

/**
 * 系统访问日志情况信息 服务层
 *
 * @author ruoyi
 */
public interface ISysLogininforService extends IService<SysLoginInfor, Long> {

	/**
	 * 清空系统登录日志
	 */
	public void cleanLogininfor();
}
