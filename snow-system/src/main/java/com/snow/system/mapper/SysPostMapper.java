package com.snow.system.mapper;

import com.snow.common.biz.IMapper;
import com.snow.system.domain.SysPost;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 岗位信息 数据层
 *
 * @author ruoyi
 */
public interface SysPostMapper extends IMapper<SysPost, Long> {

	/**
	 * 根据主键ID查询
	 *
	 * @param id 主键ID
	 * @return 返回数据对象
	 */
	@Override
	public Mono<SysPost> selectById(Long id);

	/**
	 * 分页查询
	 *
	 * @param where 条件
	 * @return 查询结果
	 */
	@Override
	public Flux<SysPost> selectList(SysPost where);

	/**
	 * 查询所有岗位
	 *
	 * @return 岗位列表
	 */
	public Flux<SysPost> selectPostAll();

	/**
	 * 根据用户ID获取岗位选择框列表
	 *
	 * @param userId 用户ID
	 * @return 选中岗位ID列表
	 */
	public Flux<Long> selectPostListByUserId(Long userId);

	/**
	 * 查询用户所属岗位组
	 *
	 * @param userName 用户名
	 * @return 结果
	 */
	public Flux<SysPost> selectPostsByUserName(String userName);

	/**
	 * 校验岗位名称
	 *
	 * @param postName 岗位名称
	 * @return 结果
	 */
	public Mono<SysPost> checkPostNameUnique(String postName);

	/**
	 * 校验岗位编码
	 *
	 * @param postCode 岗位编码
	 * @return 结果
	 */
	public Mono<SysPost> checkPostCodeUnique(String postCode);
}
