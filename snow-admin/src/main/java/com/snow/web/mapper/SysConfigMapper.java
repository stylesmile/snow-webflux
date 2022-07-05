package com.snow.web.mapper;

import com.snow.common.biz.IMapper;
import com.snow.web.domain.SysConfig;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 参数配置 数据层
 *
 * @author ruoyi
 */
public interface SysConfigMapper extends IMapper<SysConfig, Integer> {

	/**
	 * 根据主键ID查询
	 *
	 * @param id 主键ID
	 * @return 返回数据对象
	 */
	@Override
	public Mono<SysConfig> selectById(Integer id);

	/**
	 * 分页查询
	 *
	 * @param where 条件
	 * @return 查询结果
	 */
	@Override
	public Flux<SysConfig> selectList(SysConfig where);

	/**
	 * 查询参数配置信息
	 *
	 * @param config 参数配置信息
	 * @return 参数配置信息
	 */
	public Mono<SysConfig> selectConfig(SysConfig config);

	/**
	 * 根据键名查询参数配置信息
	 *
	 * @param configKey 参数键名
	 * @return 参数配置信息
	 */
	public Mono<SysConfig> checkConfigKeyUnique(String configKey);

}
