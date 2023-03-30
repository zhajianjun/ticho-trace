package top.ticho.trace.server.controller;

import com.ticho.boot.json.util.JsonUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
@RestController
@RequestMapping("log")
public class LogController {

    @PostMapping("collect")
    public void collect(@RequestBody List<Map<String, Object>> logs) {
        logs.stream().sorted(Comparator.comparing(this::sortMapping)).forEach(x -> System.out.println("日志收集：" + JsonUtil.toJsonString(x)));
    }

    private Long sortMapping(Map<String, Object> x) {
        String dtTimeStr = x.get("dtTime").toString();
        String seqStr = x.get("seq").toString();
        Long dtTime = Long.valueOf(dtTimeStr);
        Long seq = Long.valueOf(dtTimeStr);
        return dtTime + seq;
    }

}
