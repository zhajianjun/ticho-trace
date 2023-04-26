package com.ticho.trace.server;

import cn.easyes.starter.register.EsMapperScan;
import com.ticho.boot.security.annotation.EnableOauth2AuthServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


/**
 * 服务器应用程序
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
@SpringBootApplication
@EsMapperScan("com.ticho.trace.server.infrastructure.mapper")
@EnableOauth2AuthServer
@EnableAsync
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

}