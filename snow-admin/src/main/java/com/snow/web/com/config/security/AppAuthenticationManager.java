package com.snow.web.com.config.security;

import com.snow.web.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AppAuthenticationManager  {

	@Autowired
	UserDetailsServiceImpl userDetailsService;

	/**
	 * 强散列哈希加密实现
	 */
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public ReactiveAuthenticationManager reactiveAuthenticationManager() {
		UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
			new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
		authenticationManager.setPasswordEncoder(bCryptPasswordEncoder());
		return authenticationManager;
	}
}
