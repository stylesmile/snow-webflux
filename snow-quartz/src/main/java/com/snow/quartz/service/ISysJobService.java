package com.snow.quartz.service;

import com.snow.common.biz.IService;
import com.snow.quartz.domain.SysJob;
import reactor.core.publisher.Mono;

/**
 * 定时任务调度信息信息 服务层
 *
 * @author ruoyi
 */
public interface ISysJobService extends IService<SysJob, Long> {

	/**
	 * 暂停任务
	 *
	 * @param job 调度信息
	 * @return 结果
	 */
	public Mono<Long> pauseJob(SysJob job);

	/**
	 * 恢复任务
	 *
	 * @param job 调度信息
	 * @return 结果
	 */
	public Mono<Long> resumeJob(SysJob job);

	/**
	 * 任务调度状态修改
	 *
	 * @param job 调度信息
	 * @return 结果
	 */
	public Mono<Long> changeStatus(SysJob job);

	/**
	 * 删除一个任务
	 *
	 * @param job 调度信息
	 * @return 结果
	 */
	public Mono<Long> deleteJob(SysJob job);

	/**
	 * 立即运行任务
	 *
	 * @param job 调度信息
	 * @return 结果
	 */
	public Mono<Long> run(SysJob job);

	/**
	 * 校验cron表达式是否有效
	 *
	 * @param cronExpression 表达式
	 * @return 结果
	 */
	public boolean checkCronExpressionIsValid(String cronExpression);
}
