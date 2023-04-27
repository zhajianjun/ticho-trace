package com.ticho.trace.server.interfaces.facade;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.ticho.boot.view.core.PageResult;
import com.ticho.boot.view.core.Result;
import com.ticho.trace.server.application.service.SystemService;
import com.ticho.trace.server.interfaces.dto.SystemDTO;
import com.ticho.trace.server.interfaces.query.SystemQuery;
import com.ticho.trace.server.interfaces.vo.SystemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

/**
 * 系统信息
 *
 * @author zhajianjun
 * @date 2023-04-23 20:56:10
 */
@RestController
@RequestMapping("system")
@Api(tags = "系统信息")
@ApiSort(20)
public class SystemController {

    @Autowired
    private SystemService systemService;

    @PreAuthorize("@user.hasPerms('admin')")
    @ApiOperation(value = "保存系统信息")
    @ApiOperationSupport(order = 10)
    @PostMapping
    public Result<Void> save(@RequestBody SystemDTO systemDTO) {
        systemService.save(systemDTO);
        return Result.ok();
    }

    // @formatter:off
    @PreAuthorize("@user.hasPerms('admin')")
    @ApiOperation(value = "系统信息状态更改")
    @ApiOperationSupport(order = 20)
    @ApiImplicitParams({
        @ApiImplicitParam(value = "编号", name = "id", required = true),
        @ApiImplicitParam(value = "状态", name = "status", required = true),
    })
    @DeleteMapping
    public Result<Void> removeById(@RequestParam("id") String id, @RequestParam("status") Integer status) {
        systemService.updateStatusById(id, status);
        return Result.ok();
    }
    // @formatter:off

    @PreAuthorize("@user.hasPerms('admin')")
    @ApiOperation(value = "修改系统信息")
    @ApiOperationSupport(order = 30)
    @PutMapping
    public Result<Void> update(@RequestBody SystemDTO systemDTO) {
        systemService.updateById(systemDTO);
        return Result.ok();
    }

    @ApiOperation(value = "主键查询系统信息")
    @ApiOperationSupport(order = 40)
    @ApiImplicitParam(value = "编号", name = "id", required = true)
    @GetMapping
    public Result<SystemVO> getById(@RequestParam("id") Serializable id) {
        return Result.ok(systemService.getById(id));
    }

    @ApiOperation(value = "秘钥查询系统信息")
    @ApiOperationSupport(order = 50)
    @ApiImplicitParam(value = "秘钥", name = "secret", required = true)
    @GetMapping("getBySecret")
    public Result<SystemVO> getCacheBySecret(@RequestParam("secret") String secret) {
        return Result.ok(systemService.getCacheBySecret(secret));
    }

    @ApiOperation(value = "分页查询系统信息")
    @ApiOperationSupport(order = 60)
    @GetMapping("page")
    public Result<PageResult<SystemVO>> page(SystemQuery query) {
        return Result.ok(systemService.page(query));
    }

}
