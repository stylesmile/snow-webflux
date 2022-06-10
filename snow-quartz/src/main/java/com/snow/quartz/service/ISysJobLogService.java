package com.snow.quartz.service;

import com.snow.common.biz.IService;
import com.snow.quartz.domain.SysJobLog;
import reactor.core.publisher.Mono;

/**
 * 定时任务调度日志信息信息 服务层
 *
 * @author ruoyi
 */
public interface ISysJobLogService extends IService<SysJobLog, Long> {
	/**
	 * 清空任务日志
	 *
	 * @return
	 */
	public Mono<Long> cleanJobLog();
}
