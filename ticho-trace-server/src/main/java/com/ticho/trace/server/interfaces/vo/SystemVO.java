package com.ticho.trace.server.interfaces.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统信息VO
 *
 * @author zhajianjun
 * @date 2023-04-23 14:22
 */
@Data
@ApiModel(value = "系统信息VO")
public class SystemVO {

    /** 主键编号 */
    @ApiModelProperty(value = "主键编号", position = 10)
    private String id;
    /** 系统id */
    @ApiModelProperty(value = "系统id", position = 20)
    private String systemId;
    /** 系统名称 */
    @ApiModelProperty(value = "系统名称", position = 30)
    private String systemName;
    /** 秘钥 */
    @ApiModelProperty(value = "秘钥", position = 40)
    private String secret;
    /** 系统状态;1-正常,2-未激活,3-已锁定,4-已注销 */
    @ApiModelProperty(value = "系统状态", notes = "1-正常,2-未激活,3-已锁定,4-已注销", position = 50)
    private Integer status;
    /** 备注信息 */
    @ApiModelProperty(value = "备注信息", position = 60)
    private String remark;
    /** 创建人 */
    @ApiModelProperty(value = "创建人", position = 70)
    private String createBy;
    /** 创建时间 */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间", position = 80)
    private LocalDateTime createTime;
    /** 更新人 */
    @ApiModelProperty(value = "更新人", position = 90)
    private String updateBy;
    /** 更新时间 */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    @ApiModelProperty(value = "更新时间", position = 100)
    private LocalDateTime updateTime;


}
