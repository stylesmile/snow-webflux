package com.snow.web.mapper;

import com.snow.common.biz.IMapper;
import com.snow.web.domain.SysNotice;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 通知公告表 数据层
 *
 * @author ruoyi
 */
public interface SysNoticeMapper extends IMapper<SysNotice, Long> {
	/**
	 * 根据主键ID查询
	 *
	 * @param id 主键ID
	 * @return 返回数据对象
	 */
	@Override
	public Mono<SysNotice> selectById(Long id);

	/**
	 * 分页查询
	 *
	 * @param where 条件
	 * @return 查询结果
	 */
	@Override
	public Flux<SysNotice> selectList(SysNotice where);
}
