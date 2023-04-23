package com.ticho.trace.server.interfaces.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统信息查询条件
 *
 * @author zhajianjun
 * @date 2023-04-23 14:22
 */
@Data
public class SystemQuery {

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
    /** 创建人 */
    @ApiModelProperty(name = "创建人", position = 70)
    private String createBy;
    /** 创建时间 */
    @ApiModelProperty(name = "创建时间", position = 80)
    private LocalDateTime createTime;
    /** 更新人 */
    @ApiModelProperty(name = "更新人", position = 90)
    private String updateBy;
    /** 更新时间 */
    @ApiModelProperty(name = "更新时间", position = 100)
    private LocalDateTime updateTime;

}
