package top.ticho.trace.server.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ticho.trace.server.service.LogService;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 日志控制器
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
@RestController
@RequestMapping("log")
public class LogController {

    @Resource
    private LogService logService;

    @PostMapping("collect")
    public int collect(@RequestBody List<Map<String, Object>> logs) {
        return logService.collect(logs);
    }


}
