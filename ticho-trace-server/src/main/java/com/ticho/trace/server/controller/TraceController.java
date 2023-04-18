package com.ticho.trace.server.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.ticho.boot.view.core.Result;
import com.ticho.trace.server.dto.TraceDTO;
import com.ticho.trace.server.service.TraceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 链路
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
@RestController
@RequestMapping("trace")
@Api(tags = "链路")
@ApiSort(20)
public class TraceController {
    @Autowired
    private TraceService traceService;

    @PostMapping("collect")
    @ApiOperation(value = "链路收集")
    @ApiOperationSupport(order = 10)
    public void collect(@RequestBody TraceDTO traceDto) {
        traceService.collect(traceDto);
    }

    @GetMapping("getByTraceId")
    @ApiOperation(value = "链路查询")
    @ApiOperationSupport(order = 20)
    public Result<List<TraceDTO>> getByTraceId(String traceId) {
        return Result.ok(traceService.getByTraceId(traceId));
    }

}
