package com.ticho.trace.client2.feign;

import com.ticho.boot.view.core.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-04-09 22:06:04
 */
@FeignClient(name = "ticho-trace-client", url = "http://127.0.0.1:8080", path = "client")
public interface ClientProvide {

    @GetMapping("get/{id}")
    @ApiOperation(value = "根据城市id获取")
    Result<String> city(@PathVariable("id") String id);

}
