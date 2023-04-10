package top.ticho.trace.server.controller;

import com.ticho.boot.view.core.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ticho.trace.common.bean.LogInfo;
import top.ticho.trace.server.service.LogService;

import javax.annotation.Resource;
import java.util.List;

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
    public Result<Integer> collect(@RequestBody List<LogInfo> logs) {
        return Result.ok(logService.collect(logs));
    }


}
