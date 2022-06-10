package com.snow.system.service.impl;

import com.snow.common.biz.BaseServiceImpl;
import com.snow.common.constant.UserConstants;
import com.snow.common.core.domain.entity.SysDictData;
import com.snow.common.core.domain.entity.SysDictType;
import com.snow.common.exception.ServiceException;
import com.snow.common.utils.DictUtils;
import com.snow.common.utils.StringUtils;
import com.snow.system.mapper.SysDictDataMapper;
import com.snow.system.mapper.SysDictTypeMapper;
import com.snow.system.service.ISysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字典 业务层处理
 *
 * @author ruoyi
 */
@Service
public class SysDictTypeServiceImpl
	extends BaseServiceImpl<SysDictType, Long, SysDictTypeMapper>
	implements ISysDictTypeService {

	@Autowired
	private SysDictDataMapper dictDataMapper;

	/**
	 * 项目启动时，初始化字典到缓存
	 */
	@PostConstruct
	public void init() {
		loadingDictCache().subscribe();
	}

	/**
	 * 根据所有字典类型
	 *
	 * @return 字典类型集合信息
	 */
	@Override
	public Mono<List<SysDictType>> selectDictTypeAll() {
		return mapper.selectDictTypeAll().collectList();
	}

	/**
	 * 根据字典类型查询字典数据
	 *
	 * @param dictType 字典类型
	 * @return 字典数据集合信息
	 */
	@Override
	public Mono<List<SysDictData>> selectDictDataByType(String dictType) {
		return Mono.just(DictUtils.getDictCache(dictType)).flatMap(dictDatas -> {
			if (StringUtils.isNotEmpty(dictDatas)) {
				return Mono.just(dictDatas);
			}
			return dictDataMapper.selectDictDataByType(dictType).collectList().doOnSuccess(dbDictDatas -> {
				if (StringUtils.isNotEmpty(dbDictDatas)) {
					DictUtils.setDictCache(dictType, dbDictDatas);
				}
			});
		});
	}

	/**
	 * 根据字典类型查询信息
	 *
	 * @param dictType 字典类型
	 * @return 字典类型
	 */
	@Override
	public Mono<SysDictType> selectDictTypeByType(String dictType) {
		return mapper.selectDictTypeByType(dictType);
	}

	/**
	 * 批量删除字典类型信息
	 *
	 * @param dictIds 需要删除的字典ID
	 * @return
	 */
	@Override
	public Mono<Long> deleteByIds(Long[] dictIds) {
		return Flux.fromArray(dictIds).flatMap(dictId -> {
			return selectById(dictId
			).flatMap(dictType -> {
				return dictDataMapper.countDictDataByType(dictType.getDictType())
					.flatMap(count -> {
						if (count > 0) {
							throw new ServiceException(String.format("%1$s已分配,不能删除", dictType.getDictName()));
						}
						return mapper.deleteById(dictId).doOnSuccess(o -> {
							DictUtils.removeDictCache(dictType.getDictType());
						});
					});
			});
		}).count();
	}

	/**
	 * 加载字典缓存数据
	 *
	 * @return
	 */
	@Override
	public Mono<Long> loadingDictCache() {
		SysDictData dictData = new SysDictData();
		dictData.setStatus("0");
		return dictDataMapper.selectList(dictData).collectList().map(data -> {
			Map<String, List<SysDictData>> dictDataMap = data.stream().collect(
				Collectors.groupingBy(SysDictData::getDictType));
			for (Map.Entry<String, List<SysDictData>> entry : dictDataMap.entrySet()) {
				DictUtils.setDictCache(entry.getKey(), entry.getValue().stream().sorted(
					Comparator.comparing(SysDictData::getDictSort)).collect(Collectors.toList()));
			}
			return 1L;
		});
	}

	/**
	 * 清空字典缓存数据
	 */
	@Override
	public void clearDictCache() {
		DictUtils.clearDictCache();
	}

	/**
	 * 重置字典缓存数据
	 *
	 * @return
	 */
	@Override
	public Mono<Long> resetDictCache() {
		clearDictCache();
		return loadingDictCache();
	}

	/**
	 * 新增保存字典类型信息
	 *
	 * @param dict 字典类型信息
	 * @return 结果
	 */
	@Override
	public Mono<Long> insert(SysDictType dict) {
		return mapper.insert(dict).doOnSuccess(row -> {
			if (row > 0) {
				DictUtils.setDictCache(dict.getDictType(), null);
			}
		});
	}

	/**
	 * 修改保存字典类型信息
	 *
	 * @param dict 字典类型信息
	 * @return 结果
	 */
	@Override
	@Transactional
	public Mono<Long> update(SysDictType dict) {
		return mapper.selectById(dict.getDictId()).flatMap(oldDict -> {
			return dictDataMapper.updateDictDataType(oldDict.getDictType(), dict.getDictType());
		}).flatMap(o -> {
			return mapper.update(dict);
		}).flatMap(row -> {
			if (row > 0) {
				return dictDataMapper.selectDictDataByType(dict.getDictType())
					.collectList()
					.doOnSuccess(dictDatas -> {
							DictUtils.setDictCache(dict.getDictType(), dictDatas);
						}
					).thenReturn(row);
			}
			return Mono.just(row);
		});
	}

	/**
	 * 校验字典类型称是否唯一
	 *
	 * @param dict 字典类型
	 * @return 结果
	 */
	@Override
	public Mono<String> checkDictTypeUnique(SysDictType dict) {
		Long dictId = StringUtils.isNull(dict.getDictId()) ? -1L : dict.getDictId();
		return mapper.checkDictTypeUnique(dict.getDictType()
		).map(dictType -> {
			if (StringUtils.isNotNull(dictType) && dictType.getDictId().longValue() != dictId.longValue()) {
				return UserConstants.NOT_UNIQUE;
			}
			return UserConstants.UNIQUE;
		});
	}
}
