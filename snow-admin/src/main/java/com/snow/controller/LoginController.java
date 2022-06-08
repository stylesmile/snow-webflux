package com.snow.controller;

import com.snow.entity.SysUser;
import com.snow.repository.UserRepository;
import com.snow.vo.LoginVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.function.Supplier;

@RestController
public class LoginController {

    //    @Resource
//    LoginService loginService;
    @Resource
    UserRepository userRepository;


    /**
     * 启动反应式函数，返回
     */
    protected <T> Mono<T> startMono(Supplier<? extends Mono<? extends T>> supplier) {
        return Mono.defer(supplier);
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Mono<String> login(@RequestBody LoginVo loginVo) {
        return startMono(() -> {
            return userRepository.findByUsername(loginVo.getUsername());
        }).flatMap(user -> {
//            if (user == null) {
//                return new RuntimeException("用户不存在");
//            }
            return Mono.just("1");
        });

    }

    @Operation(summary = "登录")
    @PostMapping("/test")
    public Mono<SysUser> test(@RequestBody LoginVo loginVo) {
        return userRepository.findByUsername(loginVo.getUsername());
    }
}
