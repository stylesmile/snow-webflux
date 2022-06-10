package com.snow.generator.service;

import com.snow.common.biz.BaseServiceImpl;
import com.snow.generator.domain.GenTableColumn;
import com.snow.generator.mapper.GenTableColumnMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 业务字段 服务层实现
 *
 * @author ruoyi
 */
@Service
public class GenTableColumnServiceImpl
	extends BaseServiceImpl<GenTableColumn, Long, GenTableColumnMapper>
	implements IGenTableColumnService {
	/**
	 * 查询业务字段列表
	 *
	 * @param tableId 业务字段编号
	 * @return 业务字段集合
	 */
	@Override
	public Mono<List<GenTableColumn>> selectGenTableColumnListByTableId(Long tableId) {
		return mapper.selectGenTableColumnListByTableId(tableId).collectList();
	}

}
