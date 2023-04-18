package com.ticho.trace.server.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.ticho.boot.view.core.EsPageResult;
import com.ticho.boot.view.core.Result;
import com.ticho.trace.common.bean.LogInfo;
import com.ticho.trace.server.dto.LogDTO;
import com.ticho.trace.server.query.LogQuery;
import com.ticho.trace.server.service.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 日志控制器
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
@RestController
@RequestMapping("log")
@Api(tags = "日志")
@ApiSort(10)
public class LogController {

    @Resource
    private LogService logService;

    @PostMapping("collect")
    @ApiOperation(value = "日志收集")
    @ApiOperationSupport(order = 10)
    public Result<Void> collect(@RequestBody List<LogDTO> logs) {
        logService.collect(logs);
        return Result.ok();
    }

    @PostMapping("page")
    @ApiOperation(value = "日志查询")
    @ApiOperationSupport(order = 20)
    public Result<EsPageResult<Map<String, Object>>> page(@RequestBody LogQuery logQuery) {
        return Result.ok(logService.page(logQuery));
    }

}
