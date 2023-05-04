package com.ticho.trace.server.interfaces.facade;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.ticho.boot.security.annotation.IgnoreJwtCheck;
import com.ticho.boot.view.core.PageResult;
import com.ticho.boot.view.core.Result;
import com.ticho.trace.server.application.service.UserService;
import com.ticho.trace.server.interfaces.dto.AdminUserDTO;
import com.ticho.trace.server.interfaces.dto.UserDTO;
import com.ticho.trace.server.interfaces.dto.UserPasswordDTO;
import com.ticho.trace.server.interfaces.query.UserQuery;
import com.ticho.trace.server.interfaces.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
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
 * 用户信息 控制器
 *
 * @author zhajianjun
 * @date 2023-04-20 23:10
 */
@RestController
@RequestMapping("user")
@Api(tags = "用户信息")
@ApiSort(10)
public class UserController {

    @Autowired
    private UserService userService;

    @IgnoreJwtCheck
    @ApiOperation(value = "管理员用户初始化")
    @ApiOperationSupport(order = 5)
    @PostMapping("adminUserInit")
    public Result<Void> adminUserInit(@RequestBody AdminUserDTO adminUserDTO) {
        userService.adminUserInit(adminUserDTO);
        return Result.ok();
    }

    @PreAuthorize("@user.hasPerms('admin')")
    @ApiOperation(value = "保存用户信息")
    @ApiOperationSupport(order = 10)
    @PostMapping
    public Result<Void> save(@RequestBody UserDTO userDTO) {
        userService.save(userDTO);
        return Result.ok();
    }

    @PreAuthorize("@user.hasPerms('admin')")
    @ApiOperation(value = "删除用户信息")
    @ApiOperationSupport(order = 20)
    @ApiImplicitParam(value = "编号", name = "id", required = true)
    @DeleteMapping
    public Result<Void> removeById(@RequestParam("id") Serializable id) {
        userService.removeById(id);
        return Result.ok();
    }

    @PreAuthorize("@user.hasPerms('admin')")
    @ApiOperation(value = "修改用户信息")
    @ApiOperationSupport(order = 30)
    @PutMapping
    public Result<Void> update(@RequestBody UserDTO userDTO) {
        userService.updateById(userDTO);
        return Result.ok();
    }

    @ApiOperation(value = "修改用户密码")
    @ApiOperationSupport(order = 30)
    @PutMapping("updatePassword")
    public Result<Void> updatePassword(@RequestBody UserPasswordDTO userPasswordDTO) {
        userService.updatePassword(userPasswordDTO);
        return Result.ok();
    }

    @ApiOperation(value = "主键查询用户信息")
    @ApiOperationSupport(order = 40)
    @ApiImplicitParam(value = "编号", name = "id", required = true)
    @GetMapping
    public Result<UserVO> getById(@RequestParam("id") Serializable id) {
        return Result.ok(userService.getById(id));
    }

    @ApiOperation(value = "根据用户名查询用户信息")
    @ApiOperationSupport(order = 40)
    @ApiImplicitParam(value = "用户名", name = "username", required = true)
    @GetMapping("getByUsername")
    public Result<UserVO> getByUsername(@RequestParam("username") String username) {
        return Result.ok(userService.getByUsername(username));
    }

    @ApiOperation(value = "分页查询用户信息")
    @ApiOperationSupport(order = 50)
    @GetMapping("page")
    public Result<PageResult<UserVO>> page(UserQuery query) {
        return Result.ok(userService.page(query));
    }

}
