package top.ticho.trace.client.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
@RestController
@RequestMapping("client")
@Slf4j
public class CilentController {


    @PostMapping("log")
    public Object collect(@RequestBody List<Map<String, Object>> logs) {
        return logs;
    }

}
