package com.snow.quartz.service.impl;

import com.snow.common.biz.BaseServiceImpl;
import com.snow.quartz.domain.SysJobLog;
import com.snow.quartz.mapper.SysJobLogMapper;
import com.snow.quartz.service.ISysJobLogService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 定时任务调度日志信息 服务层
 *
 * @author ruoyi
 */
@Service
public class SysJobLogServiceImpl
	extends BaseServiceImpl<SysJobLog, Long, SysJobLogMapper>
	implements ISysJobLogService {

	/**
	 * 清空任务日志
	 *
	 * @return
	 */
	@Override
	public Mono<Long> cleanJobLog() {
		return mapper.cleanJobLog();
	}
}
