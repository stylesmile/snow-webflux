package com.stylesmile.modules.system.service;

import org.springframework.data.domain.Page;
import com.stylesmile.modules.system.entity.SysRole;
import com.stylesmile.modules.system.vo.query.SysRoleQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpSession;

/**
 * 用户管理
 *
 * @author chenye
 * @date 2019/1/8
 */
public interface SysUserRoleService{

    /**
     * 用户增加角色
     *
     * @param useId 用户id
     * @param roleIds 角色id
     * @return Boolean
     */
    Boolean addRole(Integer useId, String roleIds, HttpSession session);

    /**
     * 删除角色
     *
     * @param id 主键
     * @return Boolean
     */
    Mono<Void> deleteRole(Integer id);

    /**
     * 通过用户id 查询该用户拥有的角色
     *
     * @param sysRoleQuery 用户id
     * @return Page<SysRole>
     */
    Flux<SysRole> getUserRoleList(SysRoleQuery sysRoleQuery);
}
