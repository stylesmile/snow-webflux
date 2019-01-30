package com.stylesmile.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stylesmile.common.service.BaseServiceImpl;
import com.stylesmile.system.constant.UserConstant;
import com.stylesmile.system.entity.SysRole;
import com.stylesmile.system.entity.SysUser;
import com.stylesmile.system.entity.SysUserRole;
import com.stylesmile.system.mapper.SysUserRoleMapper;
import com.stylesmile.system.query.SysRoleQuery;
import com.stylesmile.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户管理
 *
 * @author chenye
 * @date 2019/1/8
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service("sysUserRoleService")
public class SysUserRoleServiceImpl extends BaseServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    SysUserRoleMapper sysUserRoleMapper;

    /**
     * 用户添加角色
     *
     * @param roleIds 以逗号分隔的角色id字符串
     * @param session session
     * @return Boolean
     */
    @Override
    public Boolean addRole(String roleIds, HttpSession session) {
        String[] roleIdss = roleIds.split(",");
        List<SysUserRole> sysUserRoleList = new ArrayList<>();
        SysUser user = (SysUser) session.getAttribute(UserConstant.LOGIN_USER);
        String userId = user.getId();
        for (String roleId : roleIdss) {
            SysUserRole userRole = new SysUserRole(UUIDUtil.getUUID(), userId, roleId);
            sysUserRoleList.add(userRole);
        }
        if (sysUserRoleList.size() == 0) {
            return false;
        }
        this.saveBatch(sysUserRoleList);
        return true;
    }

    @Override
    public Boolean deleteRole(String id) {
        return sysUserRoleMapper.deleteById(id) > 0 ? true : false;
    }

    /**
     * 通过用户id 查询 该用户拥有的角色
     *
     * @param sysRoleQuery 用户id
     * @return Page<SysRole>
     */
    @Override
    public Page<SysRole> getUserRoleList(SysRoleQuery sysRoleQuery) {
        return baseMapper.getUserRoleList(sysRoleQuery);
    }
}