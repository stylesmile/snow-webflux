package com.snow.system.service;

import java.util.List;

import com.snow.common.biz.IService;
import com.snow.system.domain.SysPost;
import reactor.core.publisher.Mono;

/**
 * 岗位信息 服务层
 *
 * @author ruoyi
 */
public interface ISysPostService extends IService<SysPost, Long> {

	/**
	 * 查询所有岗位
	 *
	 * @return 岗位列表
	 */
	public Mono<List<SysPost>> selectPostAll();


	/**
	 * 根据用户ID获取岗位选择框列表
	 *
	 * @param userId 用户ID
	 * @return 选中岗位ID列表
	 */
	public Mono<List<Long>> selectPostListByUserId(Long userId);

	/**
	 * 校验岗位名称
	 *
	 * @param post 岗位信息
	 * @return 结果
	 */
	public Mono<String> checkPostNameUnique(SysPost post);

	/**
	 * 校验岗位编码
	 *
	 * @param post 岗位信息
	 * @return 结果
	 */
	public Mono<String> checkPostCodeUnique(SysPost post);

	/**
	 * 通过岗位ID查询岗位使用数量
	 *
	 * @param postId 岗位ID
	 * @return 结果
	 */
	public Mono<Long> countUserPostById(Long postId);

}
