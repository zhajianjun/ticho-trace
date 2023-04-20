package com.ticho.trace.server.interfaces.facade;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.ticho.boot.view.core.PageResult;
import com.ticho.boot.view.core.Result;
import com.ticho.trace.server.application.service.UserInfoService;
import com.ticho.trace.server.interfaces.dto.UserInfoDTO;
import com.ticho.trace.server.interfaces.query.UserInfoQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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
 * 用户信息 控制器
 *
 * @author zhajianjun
 * @date 2023-04-20 23:10
 */
@RestController
@RequestMapping("userInfo")
@Api(tags = "用户信息")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @ApiOperation(value = "保存用户信息")
    @ApiOperationSupport(order = 10)
    @PostMapping
    public Result<Void> save(@RequestBody UserInfoDTO userInfoDTO) {
        userInfoService.save(userInfoDTO);
        return Result.ok();
    }

    @ApiOperation(value = "删除用户信息")
    @ApiOperationSupport(order = 20)
    @ApiImplicitParam(value = "编号", name = "id", required = true)
    @DeleteMapping
    public Result<Void> removeById(@RequestParam("id") Serializable id) {
        userInfoService.removeById(id);
        return Result.ok();
    }

    @ApiOperation(value = "修改用户信息")
    @ApiOperationSupport(order = 30)
    @PutMapping
    public Result<Void> update(@RequestBody UserInfoDTO userInfoDTO) {
        userInfoService.updateById(userInfoDTO);
        return Result.ok();
    }

    @ApiOperation(value = "主键查询用户信息")
    @ApiOperationSupport(order = 40)
    @ApiImplicitParam(value = "编号", name = "id", required = true)
    @GetMapping
    public Result<UserInfoDTO> getById(@RequestParam("id") Serializable id) {
        return Result.ok(userInfoService.getById(id));
    }

    @ApiOperation(value = "分页查询用户信息")
    @ApiOperationSupport(order = 50)
    @GetMapping("page")
    public Result<PageResult<UserInfoDTO>> page(UserInfoQuery query) {
        return Result.ok(userInfoService.page(query));
    }

}
