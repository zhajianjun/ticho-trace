package com.ticho.trace.server.interfaces.dto;

import cn.hutool.core.lang.RegexPool;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Admin用户初始化
 *
 * @author zhajianjun
 * @date 2023-04-24 10:20
 */
@Data
@ApiModel(value = "Admin用户初始化")
public class AdminUserDTO {

    /** 密码 */
    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(value = "密码", position = 30)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /** 真实姓名 */
    @ApiModelProperty(value = "真实姓名", position = 40)
    private String realname;

    /** 邮箱 */
    @ApiModelProperty(value = "邮箱", position = 50)
    @Pattern(regexp = RegexPool.EMAIL, message = "邮箱格式不正确")
    private String email;

    /** 手机号码 */
    @ApiModelProperty(value = "手机号码", position = 60)
    private String mobile;

    /** 头像地址 */
    @ApiModelProperty(value = "头像地址", position = 70)
    private String photo;

    /** 备注信息 */
    @ApiModelProperty(value = "备注信息", position = 110)
    private String remark;

}
