package top.ticho.trace.client2.controller;

import top.ticho.boot.view.core.Result;
import top.ticho.trace.client2.feign.ClientProvide;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
@RestController
@RequestMapping("client2")
@Slf4j
@Api(tags = "测试")
public class Cilent2Controller {

    @Autowired
    private ClientProvide clientProvide;

    @GetMapping("get/{id}")
    @ApiOperation(value = "feign测试")
    public Result<String> collect(@PathVariable String id) {
        Result<String> city = clientProvide.city(id);
        return clientProvide.city(id);
    }

}
