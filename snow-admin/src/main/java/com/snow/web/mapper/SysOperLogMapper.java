package com.snow.web.mapper;

import com.snow.common.biz.IMapper;
import com.snow.web.domain.SysOperLog;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 操作日志 数据层
 *
 * @author ruoyi
 */
public interface SysOperLogMapper extends IMapper<SysOperLog, Long> {
	/**
	 * 根据主键ID查询
	 *
	 * @param id 主键ID
	 * @return 返回数据对象
	 */
	@Override
	public Mono<SysOperLog> selectById(Long id);

	/**
	 * 分页查询
	 *
	 * @param where 条件
	 * @return 查询结果
	 */
	@Override
	public Flux<SysOperLog> selectList(SysOperLog where);

	/**
	 * 清空操作日志
	 */
	public Mono<Long> cleanOperLog();
}
