package com.snow.system.mapper;

import com.snow.system.domain.SysUserPost;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 用户与岗位关联表 数据层
 *
 * @author ruoyi
 */
public interface SysUserPostMapper {
	/**
	 * 通过用户ID删除用户和岗位关联
	 *
	 * @param userId 用户ID
	 * @return 结果
	 */
	public Mono<Long> deleteUserPostByUserId(Long userId);

	/**
	 * 通过岗位ID查询岗位使用数量
	 *
	 * @param postId 岗位ID
	 * @return 结果
	 */
	public Mono<Long> countUserPostById(Long postId);

	/**
	 * 批量删除用户和岗位关联
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public Mono<Long> deleteUserPost(Long[] ids);

	/**
	 * 批量新增用户岗位信息
	 *
	 * @param userPostList 用户角色列表
	 * @return 结果
	 */
	public Mono<Long> batchUserPost(List<SysUserPost> userPostList);
}
