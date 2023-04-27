package com.ticho.trace.server.interfaces.facade;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.ticho.boot.view.core.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 加解密
 *
 * @author zhajianjun
 * @date 2023-04-27 14:04
 */
@RestController
@RequestMapping("encrypt")
@Api(tags = "加解密")
@ApiSort(50)
public class EncryptorController {

    @Autowired
    private StringEncryptor stringEncryptor;

    @PreAuthorize("@user.hasPerms('admin')")
    @ApiOperation(value = "加密")
    @ApiOperationSupport(order = 10)
    @ApiImplicitParam(value = "加密信息", name = "message", required = true)
    @GetMapping("encrypt")
    public Result<String> encrypt(@RequestParam("message") String message) {
        return Result.ok(stringEncryptor.encrypt(message));
    }

    @PreAuthorize("@user.hasPerms('admin')")
    @ApiOperation(value = "解密")
    @ApiOperationSupport(order = 20)
    @ApiImplicitParam(value = "解密信息", name = "message", required = true)
    @GetMapping("decrypt")
    public Result<String> decrypt(@RequestParam("message") String message) {
        return Result.ok(stringEncryptor.decrypt(message));
    }

}
