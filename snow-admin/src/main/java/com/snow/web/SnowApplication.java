package com.snow.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import pro.chenggang.project.reactive.mybatis.support.r2dbc.executor.DefaultReactiveMybatisExecutor;
import pro.chenggang.project.reactive.mybatis.support.r2dbc.spring.annotation.R2dbcMapperScan;

/**
 * 启动程序
 *
 * @author stylesmile
 */

@SpringBootApplication()
// bean scan path
@ComponentScan("com.snow")
// 指定要扫描的Mapper类的包的路径
@R2dbcMapperScan("com.snow.**.mapper")
public class SnowApplication {
    public static void main(String[] args) {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        DefaultReactiveMybatisExecutor a;
        SpringApplication.run(SnowApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  snow 启动成功   ლ(´ڡ`ლ)ﾞ  \n");

    }
}
