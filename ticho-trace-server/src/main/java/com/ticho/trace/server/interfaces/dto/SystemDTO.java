package com.ticho.trace.server.interfaces.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 系统信息DTO
 *
 * @author zhajianjun
 * @date 2023-04-23 14:22
 */
@Data
public class SystemDTO {

    /** 主键编号 */
    @ApiModelProperty(name = "主键编号", position = 10)
    private String id;
    /** 系统id */
    @ApiModelProperty(name = "系统id", position = 20)
    private String systemId;
    /** 系统名称 */
    @ApiModelProperty(name = "系统名称", position = 30)
    private String systemName;
    /** 秘钥 */
    @ApiModelProperty(name = "秘钥", position = 40)
    private String secret;
    /** 系统状态;1-正常,2-未激活,3-已锁定,4-已注销 */
    @ApiModelProperty(name = "系统状态", notes = "1-正常,2-未激活,3-已锁定,4-已注销", position = 50)
    private Integer status;
    /** 备注信息 */
    @ApiModelProperty(name = "备注信息", position = 60)
    private String remark;

}
