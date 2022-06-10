package com.snow.common.biz;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IMapper<E, K> {

	/**
	 * 根据主键ID查询
	 *
	 * @param id 主键ID
	 * @return 返回数据对象
	 */
	default Mono<E> selectById(K id) {
		throw new UnsupportedOperationException("selectById");
	}

	/**
	 * 获取条件查询总数
	 *
	 * @param cond 条件
	 * @return 总数
	 */
	public Mono<Long> countList(E cond);

	/**
	 * 分页查询
	 *
	 * @param cond 条件
	 * @return 查询结果
	 */
	default Flux<E> selectList(E cond) {
		throw new UnsupportedOperationException("selectList");
	}

	/**
	 * @param entity 新增对象
	 * @return 返回新增个数
	 * 新增数据
	 */
	Mono<Long> insert(E entity);

	/**
	 * @param entity 新增对象
	 * @return 修改
	 */
	Mono<Long> update(E entity);

	/**
	 * @param id 主键ID
	 * @return 根据 主键ID删除
	 */
	Mono<Long> deleteById(K id);

	/**
	 * @param idList 主键ID列表(不能为 null 以及 empty)
	 *               删除（根据ID 批量删除）
	 */
	Mono<Long> deleteByIds(K[] idList);

}