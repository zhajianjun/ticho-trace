package com.ticho.trace.server.controller;

import com.ticho.boot.view.core.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ticho.trace.server.entity.TraceInfo;
import com.ticho.trace.server.service.TraceService;

import java.util.List;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
@RestController
@RequestMapping("trace")
public class TraceController {
    @Autowired
    private TraceService traceService;

    @PostMapping("collect")
    public void collect(@RequestBody TraceInfo traceInfo) {
        traceService.collect(traceInfo);
    }

    @GetMapping("getByTraceId")
    public Result<List<TraceInfo>> getByTraceId(String traceId) {
        return Result.ok(traceService.getByTraceId(traceId));
    }

}