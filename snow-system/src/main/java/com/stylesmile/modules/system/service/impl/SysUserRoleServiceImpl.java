//package com.stylesmile.modules.system.service.impl;
//
//import com.stylesmile.modules.system.entity.SysRole;
//import com.stylesmile.modules.system.entity.SysUserRole;
//import com.stylesmile.modules.system.repository.SysUserRoleRepository;
//import com.stylesmile.modules.system.vo.query.SysRoleQuery;
//import com.stylesmile.modules.system.service.SysUserRoleService;
//import com.stylesmile.common.util.ConvertUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import javax.servlet.http.HttpSession;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 用户管理
// *
// * @author chenye
// * @date 2019/1/8
// */
//@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
//@Service("sysUserRoleService")
//public class SysUserRoleServiceImpl implements SysUserRoleService {
//    @Autowired
//    SysUserRoleRepository sysUserRoleMapper;
//
//    /**
//     * 用户添加角色
//     *
//     * @param userId  用户id
//     * @param roleIds 以逗号分隔的角色id字符串
//     * @param session session
//     * @return Boolean
//     */
//    @Override
//    public Boolean addRole(Integer userId, String roleIds, HttpSession session) {
//        List<Integer> roleIdss = ConvertUtil.strToLongList(roleIds);
//        List<SysUserRole> sysUserRoleList = new ArrayList<>();
//        for (Integer roleId : roleIdss) {
//            SysUserRole userRole = new SysUserRole(userId, roleId);
//            sysUserRoleList.add(userRole);
//        }
//        if (sysUserRoleList.size() == 0) {
//            return false;
//        }
//        sysUserRoleMapper.saveAll(sysUserRoleList);
//        return true;
//    }
//
//    @Override
//    public Mono<Void> deleteRole(Integer id) {
//        return sysUserRoleMapper.deleteById(id);
//    }
//
//    /**
//     * 通过用户id 查询 该用户拥有的角色
//     *
//     * @param sysRoleQuery 用户id
//     * @return Page<SysRole>
//     */
//    @Override
//    public Flux<SysRole> getUserRoleList(SysRoleQuery sysRoleQuery) {
//        return null;
//    }
//}
