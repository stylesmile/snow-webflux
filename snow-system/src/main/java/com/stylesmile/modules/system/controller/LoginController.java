package com.stylesmile.modules.system.controller;

import com.stylesmile.modules.system.entity.SysUser;
import com.stylesmile.modules.system.repository.SysUserRepository;
import com.stylesmile.modules.system.vo.LoginVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.function.Supplier;

@RestController
public class LoginController {

    @Resource
    SysUserRepository userRepository;
    @Resource
    R2dbcEntityTemplate template;


    /**
     * 启动反应式函数，返回
     */
    protected <T> Mono<T> startMono(Supplier<? extends Mono<? extends T>> supplier) {
        return Mono.defer(supplier);
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Mono<String> login(@RequestBody LoginVo loginVo) {
        return startMono(
                //查询用户
                () -> userRepository.findByUsername(loginVo.getUsername()))
                //检查用户名密码
                .flatMap(user -> checkUser(user, loginVo.getPassword())
                        //生成token
                        .flatMap(s -> Mono.just(UUID.randomUUID().toString())
                        )
                );

    }
    @Operation(summary = "登录2")
    @PostMapping("/login2")
    public Mono<String> login2(@RequestBody LoginVo loginVo) {
        return userRepository.findByUsername(loginVo.getUsername())
                //检查用户名密码
                .flatMap(user -> checkUser(user, loginVo.getPassword())
                        //生成token
                        .flatMap(s -> Mono.just(UUID.randomUUID().toString())
                        )
                );

    }

    private Mono<String> checkUser(SysUser user, String password) {
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return Mono.just("1");
    }

}
