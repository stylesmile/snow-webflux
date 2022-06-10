package com.snow.framework.config;

import org.apache.ibatis.session.ExecutorType;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import pro.chenggang.project.reactive.mybatis.support.r2dbc.spring.support.R2dbcMybatisConfigurationCustomizer;

/**
 * Mybatis支持*匹配扫描包
 */
@Component
public class MyBatisR2dbcConfig {
//    @Bean
//    public ConnectionFactoryOptionsCustomizer connectionFactoryOptionsCustomizer() {
//        return connectionFactoryOptionsBuilder -> connectionFactoryOptionsBuilder
//                .option(Option.valueOf("name"), "value");
//    }

	@Bean
	public R2dbcMybatisConfigurationCustomizer r2dbcMybatisConfigurationCustomizer() {
		return r2dbcMybatisConfiguration -> {
			r2dbcMybatisConfiguration.setUseGeneratedKeys(true);
			r2dbcMybatisConfiguration.setDefaultExecutorType(ExecutorType.REUSE);
			r2dbcMybatisConfiguration.setCacheEnabled(true);
		};
	}
}
