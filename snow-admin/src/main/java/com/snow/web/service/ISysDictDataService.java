package com.snow.web.service;

import com.snow.common.biz.IService;
import com.snow.common.core.domain.entity.SysDictData;
import reactor.core.publisher.Mono;

/**
 * 字典 业务层
 *
 * @author ruoyi
 */
public interface ISysDictDataService extends IService<SysDictData, Long> {

	/**
	 * 根据字典类型和字典键值查询字典数据信息
	 *
	 * @param dictType  字典类型
	 * @param dictValue 字典键值
	 * @return 字典标签
	 */
	public Mono<String> selectDictLabel(String dictType, String dictValue);

}
