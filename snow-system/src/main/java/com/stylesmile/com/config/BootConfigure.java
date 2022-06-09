package com.stylesmile.com.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 配置 spring-boot
 * <p>
 * Configuration 配置注解
 * EnableCaching 开启缓存
 * EnableTransactionManagement 开启事务
 */
@Configuration
@EnableCaching
@EnableTransactionManagement
public class BootConfigure {

}
