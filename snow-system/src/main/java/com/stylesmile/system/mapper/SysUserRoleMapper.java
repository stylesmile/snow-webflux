package com.stylesmile.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stylesmile.system.entity.SysRole;
import com.stylesmile.system.entity.SysUserRole;
import com.stylesmile.system.query.SysRoleQuery;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户mapper
 *
 * @author chenye
 * @date 2019/01/20
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
    /**
     * 通过用户id 查询该用户拥有的角色
     *
     * @param sysRoleQuery 用户id
     * @return Page
     */
    Page<SysRole> getUserRoleList(SysRoleQuery sysRoleQuery);
}
