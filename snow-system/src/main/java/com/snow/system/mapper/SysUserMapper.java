package com.snow.system.mapper;

import com.snow.common.biz.IMapper;
import com.snow.common.core.domain.entity.SysUser;
import org.apache.ibatis.annotations.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 用户表 数据层
 *
 * @author ruoyi
 */
public interface SysUserMapper extends IMapper<SysUser, Long> {

	/**
	 * 根据主键ID查询
	 *
	 * @param id 主键ID
	 * @return 返回数据对象
	 */
	@Override
	public Mono<SysUser> selectById(Long id);

	/**
	 * 分页查询
	 *
	 * @param where 条件
	 * @return 查询结果
	 */
	@Override
	public Flux<SysUser> selectList(SysUser where);

	/**
	 * 获取已配用户角色总数
	 *
	 * @param cond 条件
	 * @return 总数
	 */
	public Mono<Long> countAllocatedList(SysUser cond);

	/**
	 * 根据条件分页查询已配用户角色列表
	 *
	 * @param user 用户信息
	 * @return 用户信息集合信息
	 */
	public Flux<SysUser> selectAllocatedList(SysUser user);

	/**
	 * 获取未分配用户角色总数
	 *
	 * @param cond 条件
	 * @return 总数
	 */
	public Mono<Long> countUnallocatedList(SysUser cond);

	/**
	 * 根据条件分页查询未分配用户角色列表
	 *
	 * @param user 用户信息
	 * @return 用户信息集合信息
	 */
	public Flux<SysUser> selectUnallocatedList(SysUser user);

	/**
	 * 通过用户名查询用户
	 *
	 * @param userName 用户名
	 * @return 用户对象信息
	 */
	public Mono<SysUser> selectUserByUserName(String userName);

	/**
	 * 修改用户头像
	 *
	 * @param userName 用户名
	 * @param avatar   头像地址
	 * @return 结果
	 */
	public Mono<Long> updateUserAvatar(@Param("userName") String userName, @Param("avatar") String avatar);

	/**
	 * 重置用户密码
	 *
	 * @param userName 用户名
	 * @param password 密码
	 * @return 结果
	 */
	public Mono<Long> resetUserPwd(@Param("userName") String userName, @Param("password") String password);

	/**
	 * 校验用户名称是否唯一
	 *
	 * @param userName 用户名称
	 * @return 结果
	 */
	public Mono<Long> checkUserNameUnique(String userName);

	/**
	 * 校验手机号码是否唯一
	 *
	 * @param phonenumber 手机号码
	 * @return 结果
	 */
	public Mono<SysUser> checkPhoneUnique(String phonenumber);

	/**
	 * 校验email是否唯一
	 *
	 * @param email 用户邮箱
	 * @return 结果
	 */
	public Mono<SysUser> checkEmailUnique(String email);
}
