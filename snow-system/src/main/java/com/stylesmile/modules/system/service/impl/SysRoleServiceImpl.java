package com.stylesmile.modules.system.service.impl;

import org.springframework.data.domain.Page;
import com.stylesmile.modules.system.entity.SysRole;
import com.stylesmile.modules.system.repository.SysRoleRepository;
import com.stylesmile.modules.system.vo.query.SysRoleQuery;
import com.stylesmile.modules.system.service.SysRoleService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * 角色 RoleService
 *
 * @author StyleSmile
 * @date 2019/1/8
 */
@Service("sysRoleService")
public class SysRoleServiceImpl implements SysRoleService {
    @Resource
    SysRoleRepository sysRoleMapper;

    @Override
    public Page<SysRole> getRoleList(SysRoleQuery sysRoleQuery) {
        return null;
    }

    @Override
    public Boolean updateRole(SysRole user) {
        return sysRoleMapper.updateRole(user);
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return Boolean
     */
    @Override
    public Boolean deleteRole(String id) {
        return sysRoleMapper.deleteRole(id);
    }

    /**
     * 检查角色编号是否重复角色
     *
     * @param code 角色编号
     * @return Integer
     */
    @Override
    public Integer checkDuplicate(String code) {
        return sysRoleMapper.checkDuplicate(code);
    }

    @Override
    public Mono<SysRole> save(SysRole role) {
        return sysRoleMapper.save(role);
    }

}
