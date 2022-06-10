package com.snow.system.service.impl;

import com.snow.common.biz.BaseServiceImpl;
import com.snow.common.core.domain.entity.SysDictData;
import com.snow.common.utils.DictUtils;
import com.snow.system.mapper.SysDictDataMapper;
import com.snow.system.service.ISysDictDataService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 字典 业务层处理
 *
 * @author ruoyi
 */
@Service
public class SysDictDataServiceImpl
	extends BaseServiceImpl<SysDictData, Long, SysDictDataMapper>
	implements ISysDictDataService {
	/**
	 * 根据字典类型和字典键值查询字典数据信息
	 *
	 * @param dictType  字典类型
	 * @param dictValue 字典键值
	 * @return 字典标签
	 */
	@Override
	public Mono<String> selectDictLabel(String dictType, String dictValue) {
		return mapper.selectDictLabel(dictType, dictValue);
	}

	/**
	 * 批量删除字典数据信息
	 *
	 * @param dictCodes 需要删除的字典数据ID
	 */
	@Override
	public Mono<Long> deleteByIds(Long[] dictCodes) {
		return Flux.fromArray(dictCodes).flatMap(dictCode -> {
			return selectById(dictCode).flatMap(data -> {
				return mapper.deleteById(dictCode).thenReturn(data);
			}).flatMap(data -> {
				return mapper.selectDictDataByType(data.getDictType()).collectList().doOnSuccess(dictDatas -> {
					DictUtils.setDictCache(data.getDictType(), dictDatas);
				});
			});
		}).count();
	}

	/**
	 * 新增保存字典数据信息
	 *
	 * @param data 字典数据信息
	 * @return 结果
	 */
	@Override
	public Mono<Long> insert(SysDictData data) {
		return mapper.insert(data).flatMap(row -> {
			if (row > 0) {
				return mapper.selectDictDataByType(data.getDictType())
					.collectList()
					.doOnSuccess(dictDatas -> {
						DictUtils.setDictCache(data.getDictType(), dictDatas);
					}).thenReturn(row);
			}
			return Mono.just(row);
		});
	}

	/**
	 * 修改保存字典数据信息
	 *
	 * @param data 字典数据信息
	 * @return 结果
	 */
	@Override
	public Mono<Long> update(SysDictData data) {
		return mapper.update(data).flatMap(row -> {
			if (row > 0) {
				return mapper.selectDictDataByType(data.getDictType())
					.collectList()
					.doOnSuccess(dictDatas -> {
						DictUtils.setDictCache(data.getDictType(), dictDatas);
					}).thenReturn(row);
			}
			return Mono.just(row);
		});
	}
}
