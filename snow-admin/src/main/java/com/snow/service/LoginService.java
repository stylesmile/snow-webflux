//package com.snow.service;
//
//import com.snow.repository.UserRepository;
//import com.snow.vo.LoginVo;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Mono;
//
//import javax.annotation.Resource;
//
//@Service
//public class LoginService {
//    @Resource
//    UserRepository userRepository;
//
////    public Mono<Object> login(LoginVo loginVo) {
////        return validLogin(loginVo);
////    }
////
////    private Mono<String> validLogin(LoginVo loginVo) {
////        return userRepository.findByUsername(loginVo.getUsername()).flatMap(user -> {
////            if (user == null) {
////                return new RuntimeException("用户不存在");
////            }
////            return Mono.just("1");
////        });
////    }
//}
