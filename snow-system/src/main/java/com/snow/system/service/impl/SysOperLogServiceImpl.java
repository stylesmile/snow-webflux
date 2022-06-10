package com.snow.system.service.impl;

import com.snow.common.biz.BaseServiceImpl;
import com.snow.system.mapper.SysOperLogMapper;
import com.snow.system.service.ISysOperLogService;
import org.springframework.stereotype.Service;
import com.snow.system.domain.SysOperLog;

/**
 * 操作日志 服务层处理
 *
 * @author ruoyi
 */
@Service
public class SysOperLogServiceImpl
	extends BaseServiceImpl<SysOperLog, Long, SysOperLogMapper>
	implements ISysOperLogService {

	/**
	 * 清空操作日志
	 */
	@Override
	public void cleanOperLog() {
		mapper.cleanOperLog();
	}
}
