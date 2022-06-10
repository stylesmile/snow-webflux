package com.snow.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pro.chenggang.project.reactive.mybatis.support.r2dbc.executor.DefaultReactiveMybatisExecutor;
import pro.chenggang.project.reactive.mybatis.support.r2dbc.spring.annotation.R2dbcMapperScan;

/**
 * 启动程序
 *
 * @author stylesmile
 */
@SpringBootApplication()
// 指定要扫描的Mapper类的包的路径
@R2dbcMapperScan("com.snow.**.mapper")
public class RuoYiApplication {
    public static void main(String[] args) {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        DefaultReactiveMybatisExecutor a;
        SpringApplication.run(RuoYiApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  snow 启动成功   ლ(´ڡ`ლ)ﾞ  \n");

    }
}
