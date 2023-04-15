package com.ticho.trace.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


/**
 * 客户端应用程序
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
@SpringBootApplication
@EnableAsync
public class ClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }
}