package top.ticho.trace.server.interfaces.facade;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import top.ticho.boot.view.core.PageResult;
import top.ticho.boot.view.core.Result;
import top.ticho.trace.server.application.service.SystemService;
import top.ticho.trace.server.interfaces.dto.SystemDTO;
import top.ticho.trace.server.interfaces.query.SystemQuery;
import top.ticho.trace.server.interfaces.vo.SystemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.List;

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
        @ApiImplicitParam(value = "系统编号", name = "systemId", required = true),
        @ApiImplicitParam(value = "状态", name = "status", required = true),
    })
    @PutMapping("updateStatus")
    public Result<Void> updateStatus(@RequestParam("systemId") String systemId, @RequestParam("status") Integer status) {
        systemService.updateStatus(systemId, status);
        return Result.ok();
    }
    // @formatter:off

    // @formatter:off
    @PreAuthorize("@user.hasPerms('admin')")
    @ApiOperation(value = "系统秘钥更改")
    @ApiOperationSupport(order = 25)
    @ApiImplicitParams({
        @ApiImplicitParam(value = "系统编号", name = "systemId", required = true),
        @ApiImplicitParam(value = "秘钥", name = "secret", required = true),
    })
    @PutMapping("updateSecret")
    public Result<Void> updateSecret(@RequestParam("systemId") String systemId, @RequestParam("secret") String secret) {
        systemService.updateSecret(systemId, secret);
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

    @ApiOperation(value = "查询所有系统信息")
    @ApiOperationSupport(order = 70)
    @GetMapping("listAll")
    public Result<List<SystemVO>> listAll() {
        return Result.ok(systemService.listAll());
    }

}
