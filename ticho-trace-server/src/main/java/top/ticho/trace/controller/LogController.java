package top.ticho.trace.controller;

import com.ticho.boot.json.util.JsonUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-03-22 23:23:33
 */
@RestController
@RequestMapping("log")
public class LogController {

    @PostMapping("collect")
    public void collect(@RequestBody List<Map<String,Object>> logs) {
        System.out.println("日志收集：" +JsonUtil.toJsonString(logs));
    }

}
