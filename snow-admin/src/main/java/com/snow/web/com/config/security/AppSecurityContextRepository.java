package com.snow.web.com.config.security;


import com.snow.common.core.domain.model.LoginUser;
import com.snow.web.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@Component
public class AppSecurityContextRepository implements ServerSecurityContextRepository {

	private static final Logger logger = LoggerFactory.getLogger(AppSecurityContextRepository.class);

	@Autowired
	private TokenService tokenService;

	@Resource
	private ReactiveAuthenticationManager authenticationManager;

	@Override
	public Mono<Void> save(ServerWebExchange exchange, SecurityContext sc) {
		return Mono.empty();
	}

	@Override
	public Mono<SecurityContext> load(ServerWebExchange exchange) {
		try {
			ServerHttpRequest request = exchange.getRequest();
			LoginUser loginUser = tokenService.getLoginUser(request);

			if (loginUser != null) {
				tokenService.verifyToken(loginUser);
				Authentication authentication = new UsernamePasswordAuthenticationToken(
					loginUser, null, loginUser.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authentication);
//            return this.authenticationManager.authenticate(
//                    authentication).map((authentication) -> new SecurityContextImpl(authentication));
				return Mono.just(new SecurityContextImpl(authentication));
			}
		} catch (Exception e) {
			logger.debug("Exception: ", e);
		}
		return Mono.empty();
	}

}


