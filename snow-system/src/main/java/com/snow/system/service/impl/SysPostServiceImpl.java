package com.snow.system.service.impl;

import com.snow.common.biz.BaseServiceImpl;
import com.snow.common.constant.UserConstants;
import com.snow.common.exception.ServiceException;
import com.snow.common.utils.StringUtils;
import com.snow.system.domain.SysPost;
import com.snow.system.mapper.SysPostMapper;
import com.snow.system.mapper.SysUserPostMapper;
import com.snow.system.service.ISysPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 岗位信息 服务层处理
 *
 * @author ruoyi
 */
@Service
public class SysPostServiceImpl
	extends BaseServiceImpl<SysPost, Long, SysPostMapper>
	implements ISysPostService {

	@Autowired
	private SysUserPostMapper userPostMapper;

	/**
	 * 查询所有岗位
	 *
	 * @return 岗位列表
	 */
	@Override
	public Mono<List<SysPost>> selectPostAll() {
		return mapper.selectPostAll().collectList();
	}

	/**
	 * 根据用户ID获取岗位选择框列表
	 *
	 * @param userId 用户ID
	 * @return 选中岗位ID列表
	 */
	@Override
	public Mono<List<Long>> selectPostListByUserId(Long userId) {
		return mapper.selectPostListByUserId(userId).collectList();
	}

	/**
	 * 校验岗位名称是否唯一
	 *
	 * @param post 岗位信息
	 * @return 结果
	 */
	@Override
	public Mono<String> checkPostNameUnique(SysPost post) {
		Long postId = StringUtils.isNull(post.getPostId()) ? -1L : post.getPostId();
		return mapper.checkPostNameUnique(post.getPostName()).map(info -> {
			if (StringUtils.isNotNull(info) && info.getPostId().longValue() != postId.longValue()) {
				return UserConstants.NOT_UNIQUE;
			}
			return UserConstants.UNIQUE;
		});

	}

	/**
	 * 校验岗位编码是否唯一
	 *
	 * @param post 岗位信息
	 * @return 结果
	 */
	@Override
	public Mono<String> checkPostCodeUnique(SysPost post) {
		Long postId = StringUtils.isNull(post.getPostId()) ? -1L : post.getPostId();
		return mapper.checkPostCodeUnique(post.getPostCode()).map(info -> {
			if (StringUtils.isNotNull(info) && info.getPostId().longValue() != postId.longValue()) {
				return UserConstants.NOT_UNIQUE;
			}
			return UserConstants.UNIQUE;
		});
	}

	/**
	 * 通过岗位ID查询岗位使用数量
	 *
	 * @param postId 岗位ID
	 * @return 结果
	 */
	@Override
	public Mono<Long> countUserPostById(Long postId) {
		return userPostMapper.countUserPostById(postId);
	}

	/**
	 * 批量删除岗位信息
	 *
	 * @param postIds 需要删除的岗位ID
	 * @return 结果
	 */
	@Override
	public Mono<Long> deleteByIds(Long[] postIds) {
		return Flux.fromArray(postIds).flatMap(postId -> {
				return selectById(postId).flatMap(post -> {
					return countUserPostById(postId).doOnSuccess(count -> {
						if (count > 0) {
							throw new ServiceException(String.format("%1$s已分配,不能删除", post.getPostName()));
						}
					});
				});

			}).count()
			.flatMap(rows -> {
				return mapper.deleteByIds(postIds);
			});
	}

}
