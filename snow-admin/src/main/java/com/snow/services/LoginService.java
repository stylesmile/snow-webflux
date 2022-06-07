package com.snow.services;

import com.snow.daos.SysUserDao;
import com.snow.entity.SysUser;
import com.snow.vo.login.LoginVo;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.UUID;

@Service
public class LoginService {
    @Resource
    SysUserDao sysUserDao;

    public Mono<String> login(LoginVo loginVo) {
        return auth(
                sysUserDao.findByName(loginVo.getUsername()
                ), loginVo.getPassword()
        ).map(this::token2);
    }

    private Mono<String> auth(Mono<SysUser> byName, String password) {
        SysUser sysUser = byName.block();
        return Mono.just("1");
    }

    private Mono<String> token(String test) {
        return Mono.just(UUID.randomUUID().toString());
    }
    private String token2(String test) {
        return UUID.randomUUID().toString();
    }


}
