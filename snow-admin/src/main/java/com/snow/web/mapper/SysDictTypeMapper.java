package com.snow.web.mapper;

import com.snow.common.biz.IMapper;
import org.apache.ibatis.annotations.Mapper;
import com.snow.common.core.domain.entity.SysDictType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 字典表 数据层
 *
 * @author ruoyi
 */
@Mapper
public interface SysDictTypeMapper extends IMapper<SysDictType, Long> {

	/**
	 * 根据主键ID查询
	 *
	 * @param id 主键ID
	 * @return 返回数据对象
	 */
	@Override
	public Mono<SysDictType> selectById(Long id);

	/**
	 * 分页查询
	 *
	 * @param where 条件
	 * @return 查询结果
	 */
	@Override
	public Flux<SysDictType> selectList(SysDictType where);

	/**
	 * 根据所有字典类型
	 *
	 * @return 字典类型集合信息
	 */
	public Flux<SysDictType> selectDictTypeAll();

	/**
	 * 根据字典类型查询信息
	 *
	 * @param dictType 字典类型
	 * @return 字典类型
	 */
	public Mono<SysDictType> selectDictTypeByType(String dictType);

	/**
	 * 校验字典类型称是否唯一
	 *
	 * @param dictType 字典类型
	 * @return 结果
	 */
	public Mono<SysDictType> checkDictTypeUnique(String dictType);
}
