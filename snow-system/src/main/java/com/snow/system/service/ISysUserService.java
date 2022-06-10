package com.snow.system.service;

import com.snow.common.biz.IService;
import com.snow.common.core.domain.entity.SysUser;
import com.snow.common.core.page.TableDataInfo;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 用户 业务层
 *
 * @author ruoyi
 */
public interface ISysUserService extends IService<SysUser, Long> {
	/**
	 * 根据条件分页查询已分配用户角色列表
	 *
	 * @param user 用户信息
	 * @return 用户信息集合信息
	 */
	public Mono<TableDataInfo> selectAllocatedList(SysUser user);

	/**
	 * 根据条件分页查询未分配用户角色列表
	 *
	 * @param user 用户信息
	 * @return 用户信息集合信息
	 */
	public Mono<TableDataInfo> selectUnallocatedList(SysUser user);

	/**
	 * 通过用户名查询用户
	 *
	 * @param userName 用户名
	 * @return 用户对象信息
	 */
	public Mono<SysUser> selectUserByUserName(String userName);

	/**
	 * 根据用户ID查询用户所属角色组
	 *
	 * @param userName 用户名
	 * @return 结果
	 */
	public Mono<String> selectUserRoleGroup(String userName);

	/**
	 * 根据用户ID查询用户所属岗位组
	 *
	 * @param userName 用户名
	 * @return 结果
	 */
	public Mono<String> selectUserPostGroup(String userName);

	/**
	 * 校验用户名称是否唯一
	 *
	 * @param userName 用户名称
	 * @return 结果
	 */
	public Mono<String> checkUserNameUnique(String userName);

	/**
	 * 校验手机号码是否唯一
	 *
	 * @param user 用户信息
	 * @return 结果
	 */
	public Mono<String> checkPhoneUnique(SysUser user);

	/**
	 * 校验email是否唯一
	 *
	 * @param user 用户信息
	 * @return 结果
	 */
	public Mono<String> checkEmailUnique(SysUser user);

	/**
	 * 校验用户是否允许操作
	 *
	 * @param user 用户信息
	 */
	public void checkUserAllowed(SysUser user);

	/**
	 * 校验用户是否有数据权限
	 *
	 * @param userId 用户id
	 * @return
	 */
	public Mono<Long> checkUserDataScope(Long userId);

	/**
	 * 注册用户信息
	 *
	 * @param user 用户信息
	 * @return 结果
	 */
	public Mono<Boolean> registerUser(SysUser user);

	/**
	 * 用户授权角色
	 *
	 * @param userId  用户ID
	 * @param roleIds 角色组
	 * @return
	 */
	public Mono<Long> insertUserAuth(Long userId, Long[] roleIds);

	/**
	 * 修改用户状态
	 *
	 * @param user 用户信息
	 * @return 结果
	 */
	public Mono<Long> updateUserStatus(SysUser user);

	/**
	 * 修改用户基本信息
	 *
	 * @param user 用户信息
	 * @return 结果
	 */
	public Mono<Long> updateUserProfile(SysUser user);

	/**
	 * 修改用户头像
	 *
	 * @param userName 用户名
	 * @param avatar   头像地址
	 * @return 结果
	 */
	public Mono<Boolean> updateUserAvatar(String userName, String avatar);

	/**
	 * 重置用户密码
	 *
	 * @param user 用户信息
	 * @return 结果
	 */
	public Mono<Long> resetPwd(SysUser user);

	/**
	 * 重置用户密码
	 *
	 * @param userName 用户名
	 * @param password 密码
	 * @return 结果
	 */
	public Mono<Long> resetUserPwd(String userName, String password);

	/**
	 * 导入用户数据
	 *
	 * @param userList        用户数据列表
	 * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
	 * @param operName        操作用户
	 * @return 结果
	 */
	public Mono<String> importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName);
}
