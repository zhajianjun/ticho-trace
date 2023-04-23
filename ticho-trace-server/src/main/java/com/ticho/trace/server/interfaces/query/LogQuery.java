package com.ticho.trace.server.interfaces.query;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ticho.boot.view.core.BasePageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 日志查询条件
 *
 * @author zhajianjun
 * @date 2023-04-18 10:12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "日志查询条件")
public class LogQuery extends BasePageQuery {

    /** 链路id */
    @ApiModelProperty(value = "链路id", position = 10)
    private String traceId;
    /** 跨度id */
    @ApiModelProperty(value = "跨度id", position = 20)
    private String spanId;
    /** 应用名称 */
    @ApiModelProperty(value = "应用名称", position = 30)
    private String appName;
    /** 应用环境 */
    @ApiModelProperty(value = "应用环境", position = 40)
    private String env;
    /** ip */
    @ApiModelProperty(value = "ip", position = 50)
    private String ip;
    /** 上个链路的应用名称 */
    @ApiModelProperty(value = "上个链路的应用名称", position = 60)
    private String preAppName;
    /** 上个链路的Ip */
    @ApiModelProperty(value = "上个链路的Ip", position = 70)
    private String preIp;
    /** 日志级别 */
    @ApiModelProperty(value = "日志级别", position = 80)
    private String logLevel;
    /** 日志时间起 */
    @NotNull(message = "日志开始时间不能为空")
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_MS_PATTERN)
    @ApiModelProperty(value = "日志时间起", position = 90)
    private LocalDateTime startDateTime;
    /** 日志时间止 */
    @NotNull(message = "日志结束时间不能为空")
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_MS_PATTERN)
    @ApiModelProperty(value = "日志时间止", position = 100)
    private LocalDateTime endDateTime;
    /** 类名称 */
    @ApiModelProperty(value = "类名称", position = 110)
    private String className;
    /** 方法名 */
    @ApiModelProperty(value = "方法名", position = 120)
    private String method;
    /** 内容 */
    @ApiModelProperty(value = "内容", position = 130)
    private String content;

}
