package com.snow.web.com.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.session.WebSessionManager;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * spring security配置
 *
 * @author ruoyi
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {
	/**
	 * 认证失败处理类
	 */
	@Autowired
	private ServerAuthenticationEntryPointImpl loginLoseHandler;

	/**
	 * 退出处理类
	 */
	@Autowired
	private LogoutSuccessHandlerImpl logoutSuccessHandler;

	@Resource
	private ReactiveAuthenticationManager authenticationManager;

	@Autowired
	private JwtAuthenticationTokenFilter authenticationFilter;

	@Autowired
	private AppSecurityContextRepository securityContextRepository;


	/**
	 * 应用的安全配置
	 */
	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		return http
			.logout().logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler)
//                .requestCache()
//                .requestCache(NoOpServerRequestCache.getInstance())
			.and().cors().disable()
			// CSRF禁用，因为不使用session
			.csrf().disable()
			.httpBasic().disable()
			.formLogin().disable()
			.headers().frameOptions().disable().and()
			.securityContextRepository(securityContextRepository)
			.addFilterAt(authenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
			.authenticationManager(authenticationManager)
			// 认证失败处理类
			.exceptionHandling().authenticationEntryPoint(loginLoseHandler)
			// 过滤请求
			.and().authorizeExchange()
			// 对于登录 login 注册register 验证码captchaImage 允许匿名访问
			.pathMatchers("/login", "/register", "/captchaImage",
					//swagger
					"swagger-ui.html", "doc.html"
			).permitAll()
			.pathMatchers(
				HttpMethod.GET,
				"/",
				"/*.html",
				"/*/*.html",
				"/*/*.css",
				"/*/*.js",
				"/*/*.ico",
				"/profile/**"
			).permitAll()
			.pathMatchers("/swagger-ui.html").permitAll()
			.pathMatchers("/swagger-resources/**").permitAll()
			.pathMatchers("/webjars/**").permitAll()
			.pathMatchers("/*/api-docs").permitAll()
			// 除上面外的所有请求全部需要鉴权认证
			.anyExchange().authenticated()
			.and()
			.build();
	}

	@Bean
	public WebSessionManager webSessionManager() {
		// Emulate SessionCreationPolicy.STATELESS
		return exchange -> Mono.empty();
	}
}
