package com.snow.generator.mapper;

import com.snow.common.biz.IMapper;
import com.snow.generator.domain.GenTableColumn;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 业务字段 数据层
 *
 * @author ruoyi
 */
public interface GenTableColumnMapper extends IMapper<GenTableColumn, Long> {

	/**
	 * 根据表名称查询列信息
	 *
	 * @param tableName 表名称
	 * @return 列信息
	 */
	public Flux<GenTableColumn> selectDbTableColumnsByName(String tableName);

	/**
	 * 查询业务字段列表
	 *
	 * @param tableId 业务字段编号
	 * @return 业务字段集合
	 */
	public Flux<GenTableColumn> selectGenTableColumnListByTableId(Long tableId);

	/**
	 * 删除业务字段
	 *
	 * @param genTableColumns 列数据
	 * @return 结果
	 */
	public Mono<Integer> deleteGenTableColumns(List<GenTableColumn> genTableColumns);
}
