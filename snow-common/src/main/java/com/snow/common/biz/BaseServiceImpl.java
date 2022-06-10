package com.snow.common.biz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

public abstract class BaseServiceImpl<E, K, M extends IMapper<E, K>> implements IService<E, K> {

	@Autowired
	protected M mapper;

	@Override
	public Mono<E> selectById(K id) {
		return mapper.selectById(id);
	}

	@Override
	public Mono<Long> countList(E cond) {
		return mapper.countList(cond);
	}

	@Override
	public Mono<List<E>> selectList(E cond) {
		return mapper.selectList(cond).collectList();
	}

	@Override
	@Transactional
	public Mono<Long> insert(E entity) {
		return mapper.insert(entity);
	}

	@Override
	@Transactional
	public Mono<Long> update(E entity) {
		return mapper.update(entity);
	}

	@Override
	@Transactional
	public Mono<Long> deleteById(K id) {
		return mapper.deleteById(id);
	}

	@Override
	@Transactional
	public Mono<Long> deleteByIds(K[] idList) {
		return mapper.deleteByIds(idList);
	}
}
