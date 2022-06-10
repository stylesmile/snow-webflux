package com.snow.system.service;

import com.snow.common.biz.IService;
import com.snow.common.core.domain.entity.SysDictData;
import com.snow.common.core.domain.entity.SysDictType;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 字典 业务层
 *
 * @author ruoyi
 */
public interface ISysDictTypeService extends IService<SysDictType, Long> {

	/**
	 * 根据所有字典类型
	 *
	 * @return 字典类型集合信息
	 */
	public Mono<List<SysDictType>> selectDictTypeAll();

	/**
	 * 根据字典类型查询字典数据
	 *
	 * @param dictType 字典类型
	 * @return 字典数据集合信息
	 */
	public Mono<List<SysDictData>> selectDictDataByType(String dictType);

	/**
	 * 根据字典类型查询信息
	 *
	 * @param dictType 字典类型
	 * @return 字典类型
	 */
	public Mono<SysDictType> selectDictTypeByType(String dictType);

	/**
	 * 加载字典缓存数据
	 *
	 * @return
	 */
	public Mono<Long> loadingDictCache();

	/**
	 * 清空字典缓存数据
	 */
	public void clearDictCache();

	/**
	 * 重置字典缓存数据
	 *
	 * @return
	 */
	public Mono<Long> resetDictCache();

	/**
	 * 校验字典类型称是否唯一
	 *
	 * @param dictType 字典类型
	 * @return 结果
	 */
	public Mono<String> checkDictTypeUnique(SysDictType dictType);
}
