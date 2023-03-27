package top.ticho.trace.server.controller;

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
 * @date 2023-03-27 12:34
 */
@RestController
@RequestMapping("trace")
public class TraceController {

    @PostMapping("collect")
    public void collect(@RequestBody List<Map<String,Object>> logs) {
        System.out.println("链路收集：" + JsonUtil.toJsonString(logs));
    }

}
