package com.ticho.trace.server.interfaces.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 用户密码修改
 *
 * @author zhajianjun
 * @date 2023-05-04 10:28
 */
@Data
@EqualsAndHashCode()
@ApiModel("用户密码修改")
public class UserPasswordDTO {

    @ApiModelProperty(value = "账户id")
    @NotNull(message = "用户id不能为空")
    private Long id;

    @ApiModelProperty(value = "当前密码")
    @NotBlank(message = "密码不能为空")
    private String password;

    @ApiModelProperty(value = "新密码")
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, message = "密码至少6个字符")
    private String passwordNew;

}
