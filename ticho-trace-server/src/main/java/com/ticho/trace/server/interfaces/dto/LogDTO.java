package com.ticho.trace.server.interfaces.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * 日志信息DTO
 *
 * @author zhajianjun
 * @date 2023-04-18 14:45
 */
@Data
@ApiModel(value = "日志信息DTO")
public class LogDTO {

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
    /** 日志时间 */
    @ApiModelProperty(value = "日志时间", position = 90)
    private String dateTime;
    /** 日志时间戳 */
    @ApiModelProperty(value = "日志时间戳", position = 100)
    private Long dtTime;
    /** 类名称 */
    @ApiModelProperty(value = "类名称", position = 110)
    private String className;
    /** 方法名 */
    @ApiModelProperty(value = "方法名", position = 120)
    private String method;
    /** 序列号 */
    @ApiModelProperty(value = "序列号", position = 130)
    private Long seq;
    /** 内容 */
    @ApiModelProperty(value = "内容", position = 140)
    private String content;
    /** 线程名称 */
    @ApiModelProperty(value = "线程名称", position = 150)
    private String threadName;
    /** mdc信息 */
    @ApiModelProperty(value = "mdc信息", position = 160)
    private Map<String, String> mdc;

}
