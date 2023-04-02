package top.ticho.trace.server;

import cn.easyes.starter.register.EsMapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * 服务器应用程序
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
@SpringBootApplication
@EsMapperScan("top.ticho.trace.server.mapper")
public class ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}