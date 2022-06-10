package com.snow.quartz.mapper;

import com.snow.common.biz.IMapper;
import com.snow.quartz.domain.SysJobLog;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 调度任务日志信息 数据层
 *
 * @author ruoyi
 */
public interface SysJobLogMapper extends IMapper<SysJobLog, Long> {
	/**
	 * 根据主键ID查询
	 *
	 * @param id 主键ID
	 * @return 返回数据对象
	 */
	@Override
	public Mono<SysJobLog> selectById(Long id);

	/**
	 * 分页查询
	 *
	 * @param where 条件
	 * @return 查询结果
	 */
	@Override
	public Flux<SysJobLog> selectList(SysJobLog where);

	/**
	 * 获取quartz调度器日志的计划任务
	 *
	 * @param jobLog 调度日志信息
	 * @return 调度任务日志集合
	 */
	public Flux<SysJobLog> selectJobLogList(SysJobLog jobLog);

	/**
	 * 查询所有调度任务日志
	 *
	 * @return 调度任务日志列表
	 */
	public Flux<SysJobLog> selectJobLogAll();

	/**
	 * 通过调度任务日志ID查询调度信息
	 *
	 * @param jobLogId 调度任务日志ID
	 * @return 调度任务日志对象信息
	 */
	public Mono<SysJobLog> selectJobLogById(Long jobLogId);

	/**
	 * 新增任务日志
	 *
	 * @param jobLog 调度日志信息
	 * @return 结果
	 */
	public Mono<Integer> insertJobLog(SysJobLog jobLog);

	/**
	 * 批量删除调度日志信息
	 *
	 * @param logIds 需要删除的数据ID
	 * @return 结果
	 */
	public Mono<Integer> deleteJobLogByIds(Long[] logIds);

	/**
	 * 删除任务日志
	 *
	 * @param jobId 调度日志ID
	 * @return 结果
	 */
	public Mono<Integer> deleteJobLogById(Long jobId);

	/**
	 * 清空任务日志
	 */
	public Mono<Long> cleanJobLog();
}
