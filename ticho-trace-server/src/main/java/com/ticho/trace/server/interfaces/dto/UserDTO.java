package com.ticho.trace.server.interfaces.dto;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

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

    /** 账户;账户具有唯一性 */
    @ApiModelProperty(value = "账户;账户具有唯一性", position = 20)
    private String username;

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

    /** 最后登录ip地址 */
    @ApiModelProperty(value = "最后登录ip地址", position = 80)
    private String lastIp;

    /** 最后登录时间 */
    @ApiModelProperty(value = "最后登录时间", position = 90)
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_MS_PATTERN, timezone = "GMT+8")
    private LocalDateTime lastTime;

    /** 用户状态;1-正常,2-未激活,3-已锁定,4-已注销 */
    @ApiModelProperty(value = "用户状态;1-正常,2-未激活,3-已锁定,4-已注销", position = 100)
    private Integer status;

    /** 备注信息 */
    @ApiModelProperty(value = "备注信息", position = 110)
    private String remark;

}
