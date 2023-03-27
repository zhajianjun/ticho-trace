package top.ticho.trace.server.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
@RestController
@RequestMapping("trace")
public class TraceController {

    @PostMapping("collect")
    public void collect(@RequestBody Map<String, Object> traceData) {
        System.out.println("链路收集：" + traceData);
    }

}
