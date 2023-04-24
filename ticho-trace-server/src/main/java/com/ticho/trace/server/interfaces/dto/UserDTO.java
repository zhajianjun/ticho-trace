package com.ticho.trace.server.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
    @ApiModelProperty(value = "id", position = 10)
    private String id;

    /** 账户;账户具有唯一性 */
    @ApiModelProperty(value = "账户;账户具有唯一性", position = 20)
    private String username;

    /** 密码 */
    @ApiModelProperty(value = "密码", position = 30)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /** 真实姓名 */
    @ApiModelProperty(value = "真实姓名", position = 40)
    private String realname;

    /** 邮箱 */
    @ApiModelProperty(value = "邮箱", position = 50)
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
