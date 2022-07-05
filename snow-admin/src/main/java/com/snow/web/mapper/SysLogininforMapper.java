package com.snow.web.mapper;

import com.snow.common.biz.IMapper;
import com.snow.web.domain.SysLoginInfor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 系统访问日志情况信息 数据层
 *
 * @author ruoyi
 */
public interface SysLogininforMapper extends IMapper<SysLoginInfor, Long> {
	/**
	 * 根据主键ID查询
	 *
	 * @param id 主键ID
	 * @return 返回数据对象
	 */
	@Override
	public Mono<SysLoginInfor> selectById(Long id);

	/**
	 * 分页查询
	 *
	 * @param where 条件
	 * @return 查询结果
	 */
	@Override
	public Flux<SysLoginInfor> selectList(SysLoginInfor where);

	/**
	 * 清空系统登录日志
	 *
	 * @return 结果
	 */
	public Mono<Long> cleanLogininfor();
}
