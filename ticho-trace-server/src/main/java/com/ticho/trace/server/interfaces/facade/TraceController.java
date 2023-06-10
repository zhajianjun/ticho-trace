package com.ticho.trace.server.interfaces.facade;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.ticho.boot.security.annotation.IgnoreJwtCheck;
import com.ticho.boot.view.core.PageResult;
import com.ticho.boot.view.core.Result;
import com.ticho.trace.server.application.service.TraceService;
import com.ticho.trace.server.interfaces.dto.TraceDTO;
import com.ticho.trace.server.interfaces.query.TraceQuery;
import com.ticho.trace.server.interfaces.vo.TraceVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@ApiSort(40)
public class TraceController {
    @Autowired
    private TraceService traceService;

    @IgnoreJwtCheck
    @PostMapping("collect")
    @ApiOperation(value = "链路收集")
    @ApiImplicitParam(value = "秘钥信息", name = "secret", required = true, paramType = "header")
    @ApiOperationSupport(order = 10)
    public Result<Void> collect(@RequestHeader("secret") String secret, @RequestBody TraceDTO traceDto) {
        traceService.collect(secret, traceDto);
        return Result.ok();
    }

    @GetMapping("getByTraceId")
    @ApiOperation(value = "链路查询")
    @ApiOperationSupport(order = 20)
    public Result<List<TraceVO>> getByTraceId(String traceId) {
        return Result.ok(traceService.getByTraceId(traceId));
    }

    @GetMapping("page")
    @ApiOperation(value = "链路分页查询")
    @ApiOperationSupport(order = 20)
    public Result<PageResult<TraceVO>> page(TraceQuery query) {
        return Result.ok(traceService.page(query));
    }

}
