package com.snow.system.service.impl;

import com.snow.common.annotation.DataScope;
import com.snow.common.biz.BaseServiceImpl;
import com.snow.common.constant.UserConstants;
import com.snow.common.core.domain.entity.SysRole;
import com.snow.common.core.domain.entity.SysUser;
import com.snow.common.core.page.TableDataInfo;
import com.snow.common.exception.ServiceException;
import com.snow.common.utils.PageUtils;
import com.snow.common.utils.SecurityUtils;
import com.snow.common.utils.StringUtils;
import com.snow.common.utils.bean.BeanValidators;
import com.snow.common.utils.spring.SpringUtils;
import com.snow.system.domain.SysPost;
import com.snow.system.domain.SysUserPost;
import com.snow.system.domain.SysUserRole;
import com.snow.system.mapper.*;
import com.snow.system.service.ISysConfigService;
import com.snow.system.service.ISysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 用户 业务层处理
 *
 * @author ruoyi
 */
@Service
public class SysUserServiceImpl
	extends BaseServiceImpl<SysUser, Long, SysUserMapper>
	implements ISysUserService {
	private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

	@Autowired
	private SysRoleMapper roleMapper;

	@Autowired
	private SysPostMapper postMapper;

	@Autowired
	private SysUserRoleMapper userRoleMapper;

	@Autowired
	private SysUserPostMapper userPostMapper;

	@Autowired
	private ISysConfigService configService;

	@Autowired
	protected Validator validator;

	/**
	 * 根据条件分页查询用户列表
	 *
	 * @param user 用户信息
	 * @return 用户信息集合信息
	 */
	@Override
	@DataScope(deptAlias = "d", userAlias = "u")
	public Mono<List<SysUser>> selectList(SysUser user) {
		return super.selectList(user);
	}

	/**
	 * 根据条件分页查询已分配用户角色列表
	 *
	 * @param cond 用户信息
	 * @return 用户信息集合信息
	 */
	@Override
	@DataScope(deptAlias = "d", userAlias = "u")
	public Mono<TableDataInfo> selectAllocatedList(SysUser cond) {
		return mapper.selectAllocatedList(cond).collectList().flatMap(list -> {
			if (cond.isPaged()) {
				return mapper.countAllocatedList(cond).map(total -> {
					return PageUtils.getDataTable(list, total);
				});
			}
			return Mono.just(PageUtils.getDataTable(list, list.size()));
		});

	}

	/**
	 * 根据条件分页查询未分配用户角色列表
	 *
	 * @param cond 用户信息
	 * @return 用户信息集合信息
	 */
	@Override
	@DataScope(deptAlias = "d", userAlias = "u")
	public Mono<TableDataInfo> selectUnallocatedList(SysUser cond) {
		return mapper.selectUnallocatedList(cond).collectList().flatMap(list -> {
			if (cond.isPaged()) {
				return mapper.countUnallocatedList(cond).map(total -> {
					return PageUtils.getDataTable(list, total);
				});
			}
			return Mono.just(PageUtils.getDataTable(list, list.size()));
		});
	}

	/**
	 * 通过用户名查询用户
	 *
	 * @param userName 用户名
	 * @return 用户对象信息
	 */
	@Override
	public Mono<SysUser> selectUserByUserName(String userName) {
		return mapper.selectUserByUserName(userName);
	}

	/**
	 * 查询用户所属角色组
	 *
	 * @param userName 用户名
	 * @return 结果
	 */
	@Override
	public Mono<String> selectUserRoleGroup(String userName) {
		return roleMapper.selectRolesByUserName(userName)
			.collectList()
			.map(list -> {
				if (CollectionUtils.isEmpty(list)) {
					return StringUtils.EMPTY;
				}
				return list.stream().map(SysRole::getRoleName).collect(Collectors.joining(","));
			});
	}

	/**
	 * 查询用户所属岗位组
	 *
	 * @param userName 用户名
	 * @return 结果
	 */
	@Override
	public Mono<String> selectUserPostGroup(String userName) {
		return postMapper.selectPostsByUserName(userName)
			.collectList()
			.map(list -> {
				if (CollectionUtils.isEmpty(list)) {
					return StringUtils.EMPTY;
				}
				return list.stream().map(SysPost::getPostName).collect(Collectors.joining(","));
			});
	}

	/**
	 * 校验用户名称是否唯一
	 *
	 * @param userName 用户名称
	 * @return 结果
	 */
	@Override
	public Mono<String> checkUserNameUnique(String userName) {
		return mapper.checkUserNameUnique(userName).map(count -> {
			if (count > 0) {
				return UserConstants.NOT_UNIQUE;
			}
			return UserConstants.UNIQUE;
		});
	}

	/**
	 * 校验手机号码是否唯一
	 *
	 * @param user 用户信息
	 * @return
	 */
	@Override
	public Mono<String> checkPhoneUnique(SysUser user) {
		return mapper.checkPhoneUnique(user.getPhonenumber()).map(info -> {
			Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
			if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
				return UserConstants.NOT_UNIQUE;
			}
			return UserConstants.UNIQUE;
		});
	}

	/**
	 * 校验email是否唯一
	 *
	 * @param user 用户信息
	 * @return
	 */
	@Override
	public Mono<String> checkEmailUnique(SysUser user) {
		return mapper.checkEmailUnique(user.getEmail())
			.map(info -> {
				Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
				if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
					return UserConstants.NOT_UNIQUE;
				}
				return UserConstants.UNIQUE;
			});
	}

	/**
	 * 校验用户是否允许操作
	 *
	 * @param user 用户信息
	 */
	@Override
	public void checkUserAllowed(SysUser user) {
		if (StringUtils.isNotNull(user.getUserId()) && user.isAdmin()) {
			throw new ServiceException("不允许操作超级管理员用户");
		}
	}

	/**
	 * 校验用户是否有数据权限
	 *
	 * @param userId 用户id
	 * @return
	 */
	@Override
	public Mono<Long> checkUserDataScope(Long userId) {
		return SecurityUtils.getUserId().flatMap(loginUserId -> {
			if (!SysUser.isAdmin(loginUserId)) {
				SysUser user = new SysUser();
				user.setUserId(userId);
				return SpringUtils.getAopProxy(this).selectList(user)
					.doOnSuccess(users -> {
						if (StringUtils.isEmpty(users)) {
							throw new ServiceException("没有权限访问用户数据！");
						}
					}).thenReturn(1L);

			}
			return Mono.just(1L);
		});
	}

	/**
	 * 新增保存用户信息
	 *
	 * @param user 用户信息
	 * @return 结果
	 */
	@Override
	@Transactional
	public Mono<Long> insert(SysUser user) {
		// 新增用户信息
		return mapper.insert(user).flatMap(rows -> {
			// 新增用户岗位关联
			return insertUserPost(user).flatMap(o -> {
				// 新增用户与角色管理
				return insertUserRole(user);
			}).thenReturn(rows);
		});
	}

	/**
	 * 注册用户信息
	 *
	 * @param user 用户信息
	 * @return 结果
	 */
	@Override
	public Mono<Boolean> registerUser(SysUser user) {
		return mapper.insert(user).map(rows -> rows > 0);
	}

	/**
	 * 修改保存用户信息
	 *
	 * @param user 用户信息
	 * @return 结果
	 */
	@Override
	@Transactional
	public Mono<Long> update(SysUser user) {
		Long userId = user.getUserId();
		// 删除用户与角色关联
		return userRoleMapper.deleteUserRoleByUserId(userId).flatMap(o -> {
			// 新增用户与角色管理
			return insertUserRole(user);
		}).flatMap(o -> {
			// 删除用户与岗位关联
			return userPostMapper.deleteUserPostByUserId(userId);
		}).flatMap(o -> {
			// 新增用户与岗位管理
			return insertUserPost(user);
		}).flatMap(o -> {
			return mapper.update(user);
		});
	}

	/**
	 * 用户授权角色
	 *
	 * @param userId  用户ID
	 * @param roleIds 角色组
	 * @return
	 */
	@Override
	@Transactional
	public Mono<Long> insertUserAuth(Long userId, Long[] roleIds) {
		return userRoleMapper.deleteUserRoleByUserId(userId).flatMap(rows -> {
			return insertUserRole(userId, roleIds);
		});
	}

	/**
	 * 修改用户状态
	 *
	 * @param user 用户信息
	 * @return 结果
	 */
	@Override
	public Mono<Long> updateUserStatus(SysUser user) {
		return mapper.update(user);
	}

	/**
	 * 修改用户基本信息
	 *
	 * @param user 用户信息
	 * @return 结果
	 */
	@Override
	public Mono<Long> updateUserProfile(SysUser user) {
		return mapper.update(user);
	}

	/**
	 * 修改用户头像
	 *
	 * @param userName 用户名
	 * @param avatar   头像地址
	 * @return 结果
	 */
	@Override
	public Mono<Boolean> updateUserAvatar(String userName, String avatar) {
		return mapper.updateUserAvatar(userName, avatar).map(rows -> {
			return rows > 0;
		});
	}

	/**
	 * 重置用户密码
	 *
	 * @param user 用户信息
	 * @return 结果
	 */
	@Override
	public Mono<Long> resetPwd(SysUser user) {
		return mapper.update(user);
	}

	/**
	 * 重置用户密码
	 *
	 * @param userName 用户名
	 * @param password 密码
	 * @return 结果
	 */
	@Override
	public Mono<Long> resetUserPwd(String userName, String password) {
		return mapper.resetUserPwd(userName, password);
	}

	/**
	 * 新增用户角色信息
	 *
	 * @param user 用户对象
	 */
	public Mono<Long> insertUserRole(SysUser user) {
		Long[] roles = user.getRoleIds();
		if (StringUtils.isNotNull(roles)) {
			// 新增用户与角色管理
			List<SysUserRole> list = new ArrayList<>();
			for (Long roleId : roles) {
				SysUserRole ur = new SysUserRole();
				ur.setUserId(user.getUserId());
				ur.setRoleId(roleId);
				list.add(ur);
			}
			if (list.size() > 0) {
				return userRoleMapper.batchUserRole(list);
			}
		}
		return Mono.just(1L);
	}

	/**
	 * 新增用户岗位信息
	 *
	 * @param user 用户对象
	 */
	public Mono<Long> insertUserPost(SysUser user) {
		Long[] posts = user.getPostIds();
		if (StringUtils.isNotNull(posts)) {
			// 新增用户与岗位管理
			List<SysUserPost> list = new ArrayList<>();
			for (Long postId : posts) {
				SysUserPost up = new SysUserPost();
				up.setUserId(user.getUserId());
				up.setPostId(postId);
				list.add(up);
			}
			if (list.size() > 0) {
				return userPostMapper.batchUserPost(list);
			}
		}
		return Mono.just(1L);
	}

	/**
	 * 新增用户角色信息
	 *
	 * @param userId  用户ID
	 * @param roleIds 角色组
	 */
	public Mono<Long> insertUserRole(Long userId, Long[] roleIds) {
		if (StringUtils.isNotNull(roleIds)) {
			// 新增用户与角色管理
			List<SysUserRole> list = new ArrayList<>();
			for (Long roleId : roleIds) {
				SysUserRole ur = new SysUserRole();
				ur.setUserId(userId);
				ur.setRoleId(roleId);
				list.add(ur);
			}
			if (list.size() > 0) {
				return userRoleMapper.batchUserRole(list);
			}
		}
		return Mono.just(1L);
	}

	/**
	 * 通过用户ID删除用户
	 *
	 * @param userId 用户ID
	 * @return 结果
	 */
	@Override
	@Transactional
	public Mono<Long> deleteById(Long userId) {
		// 删除用户与角色关联
		return userRoleMapper.deleteUserRoleByUserId(userId
		).flatMap(o -> {
			// 删除用户与岗位表
			return userPostMapper.deleteUserPostByUserId(userId);
		}).flatMap(o -> {
			return mapper.deleteById(userId);
		});
	}

	/**
	 * 批量删除用户信息
	 *
	 * @param userIds 需要删除的用户ID
	 * @return 结果
	 */
	@Override
	@Transactional
	public Mono<Long> deleteByIds(Long[] userIds) {
		return Flux.fromArray(userIds).flatMap(userId -> {
			checkUserAllowed(new SysUser(userId));
			return checkUserDataScope(userId);
		}).count().flatMap(o -> {
			// 删除用户与角色关联
			return userRoleMapper.deleteUserRole(userIds);
		}).flatMap(o -> {
			// 删除用户与岗位关联
			return userPostMapper.deleteUserPost(userIds);
		}).flatMap(o -> {
			return mapper.deleteByIds(userIds);
		});
	}

	/**
	 * 导入用户数据
	 *
	 * @param userList        用户数据列表
	 * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
	 * @param operName        操作用户
	 * @return 结果
	 */
	@Override
	public Mono<String> importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName) {
		if (StringUtils.isNull(userList) || userList.size() == 0) {
			throw new ServiceException("导入用户数据不能为空！");
		}
		AtomicInteger successNum = new AtomicInteger(0);
		AtomicInteger failureNum = new AtomicInteger(0);
		StringBuilder successMsg = new StringBuilder();
		StringBuilder failureMsg = new StringBuilder();
		return configService.selectConfigByKey("sys.user.initPassword").flatMap(password -> {
			return Flux.fromIterable(userList).flatMap(user -> {
				// 验证是否存在这个用户
				return mapper.selectUserByUserName(user.getUserName()).flatMap(u -> {
					if (StringUtils.isNull(u)) {
						BeanValidators.validateWithException(validator, user);
						user.setPassword(SecurityUtils.encryptPassword(password));
						user.setCreateBy(operName);
						return this.insert(user)
							.doOnSuccess(o -> {
								successNum.incrementAndGet();
								successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 导入成功");
							}).doOnError(e -> {
								failureNum.incrementAndGet();
								String msg = "<br/>" + failureNum + "、账号 " + user.getUserName() + " 导入失败：";
								failureMsg.append(msg + e.getMessage());
								log.error(msg, e);
							});
					} else if (isUpdateSupport) {
						BeanValidators.validateWithException(validator, user);
						user.setUpdateBy(operName);
						return this.update(user)
							.doOnSuccess(o -> {
								successNum.incrementAndGet();
								successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 更新成功");
							}).doOnError(e -> {
								failureNum.incrementAndGet();
								String msg = "<br/>" + failureNum + "、账号 " + user.getUserName() + " 导入失败：";
								failureMsg.append(msg + e.getMessage());
								log.error(msg, e);
							});
					} else {
						failureNum.incrementAndGet();
						failureMsg.append("<br/>" + failureNum + "、账号 " + user.getUserName() + " 已存在");
						return Mono.just(0L);
					}
				}).doOnError(e -> {
					failureNum.incrementAndGet();
					String msg = "<br/>" + failureNum + "、账号 " + user.getUserName() + " 导入失败：";
					failureMsg.append(msg + e.getMessage());
					log.error(msg, e);
				});
			}).count().map(o -> {
				if (failureNum.get() > 0) {
					failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum.get() + " 条数据格式不正确，错误如下：");
					throw new ServiceException(failureMsg.toString());
				} else {
					successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum.get() + " 条，数据如下：");
				}
				return successMsg.toString();
			});
		});

	}
}
