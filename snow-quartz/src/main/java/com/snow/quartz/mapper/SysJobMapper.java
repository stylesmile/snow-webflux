package com.snow.quartz.mapper;

import com.snow.common.biz.IMapper;
import com.snow.quartz.domain.SysJob;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 调度任务信息 数据层
 *
 * @author ruoyi
 */
public interface SysJobMapper extends IMapper<SysJob, Long> {

	/**
	 * 根据主键ID查询
	 *
	 * @param id 主键ID
	 * @return 返回数据对象
	 */
	@Override
	public Mono<SysJob> selectById(Long id);

	/**
	 * 分页查询
	 *
	 * @param where 条件
	 * @return 查询结果
	 */
	@Override
	public Flux<SysJob> selectList(SysJob where);

	/**
	 * 查询所有调度任务
	 *
	 * @return 调度任务列表
	 */
	public Flux<SysJob> selectJobAll();
}
