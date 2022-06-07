package com.snow.controllers;

import com.snow.models.Employee;
import com.snow.services.EmployeeService;
import com.snow.services.LoginService;
import com.snow.vo.login.LoginVo;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * @author: ffzs
 * @Date: 2020/8/10 下午4:43
 */

@RestController
@RequiredArgsConstructor
@Slf4j
@RestControllerAdvice
public class LoginController {

    private final LoginService loginService;

    @Operation(summary = "登录")
    @PostMapping(path = "{login}")
    public Mono<String> login (@RequestBody @Valid LoginVo loginVo) {
        return loginService.login(loginVo);
    }
}
