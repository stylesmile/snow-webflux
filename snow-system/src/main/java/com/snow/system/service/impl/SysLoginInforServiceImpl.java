package com.snow.system.service.impl;

import com.snow.common.biz.BaseServiceImpl;
import com.snow.system.domain.SysLoginInfor;
import com.snow.system.mapper.SysLogininforMapper;
import com.snow.system.service.ISysLogininforService;
import org.springframework.stereotype.Service;

/**
 * 系统访问日志情况信息 服务层处理
 *
 * @author ruoyi
 */
@Service
public class SysLoginInforServiceImpl
	extends BaseServiceImpl<SysLoginInfor, Long, SysLogininforMapper>
	implements ISysLogininforService {

	/**
	 * 清空系统登录日志
	 */
	@Override
	public void cleanLogininfor() {
		mapper.cleanLogininfor();
	}
}
