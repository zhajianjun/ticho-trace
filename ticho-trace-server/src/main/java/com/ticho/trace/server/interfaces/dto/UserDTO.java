package com.ticho.trace.server.interfaces.dto;

import cn.hutool.core.lang.RegexPool;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ticho.boot.web.util.valid.ValidGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

/**
 * 用户信息DTO
 *
 * @author zhajianjun
 * @date 2023-04-20 23:10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "用户信息DTO")
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** id */
    @NotBlank(message = "id不能为空", groups = ValidGroup.Upd.class)
    @ApiModelProperty(value = "id", position = 10)
    private String id;

    /** 账户;账户具有唯一性 */
    @NotBlank(message = "用户名不能为空", groups = ValidGroup.Add.class)
    @ApiModelProperty(value = "账户;账户具有唯一性", position = 20)
    private String username;

    /** 密码 */
    @NotBlank(message = "密码不能为空", groups = ValidGroup.Add.class)
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

    /** 系统id列表 */
    @ApiModelProperty(value = "系统id列表", position = 160)
    private List<String> systemIds;

}
