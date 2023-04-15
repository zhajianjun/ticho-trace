package com.ticho.trace.client2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;


/**
 * 客户端应用程序
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.ticho.trace.client2.feign"})
@EnableAsync
public class Client2Application {
    public static void main(String[] args) {
        SpringApplication.run(Client2Application.class, args);
    }
}