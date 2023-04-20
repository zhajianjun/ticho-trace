package com.ticho.trace.server.interfaces.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 链路信息DTO
 *
 * @author zhajianjun
 * @date 2023-04-18 14:18
 */
@Data
@ApiModel(value = "链路信息DTO")
public class TraceDTO {

    /** id */
    @ApiModelProperty(value = "id", position = 10)
    private String id;
    /** 链路id */
    @ApiModelProperty(value = "链路id", position = 20)
    private String traceId;
    /** 跨度id */
    @ApiModelProperty(value = "跨度id", position = 30)
    private String spanId;
    /** 应用名称 */
    @ApiModelProperty(value = "应用名称", position = 40)
    private String appName;
    /** 应用环境 */
    @ApiModelProperty(value = "应用环境", position = 50)
    private String env;
    /** ip */
    @ApiModelProperty(value = "ip", position = 60)
    private String ip;
    /** 上个链路的应用名称 */
    @ApiModelProperty(value = "上个链路的应用名称", position = 70)
    private String preAppName;
    /** 上个链路的Ip */
    @ApiModelProperty(value = "上个链路的Ip", position = 80)
    private String preIp;
    /** 请求类型 */
    @ApiModelProperty(value = "请求类型", position = 90)
    private String type;
    /** 接口 */
    @ApiModelProperty(value = "接口", position = 100)
    private String url;
    /** 端口号 */
    @ApiModelProperty(value = "端口号", position = 110)
    private String port;
    /** 全路径接口 */
    @ApiModelProperty(value = "全路径接口", position = 120)
    private String fullUrl;
    /** 接口 */
    @ApiModelProperty(value = "接口", position = 130)
    private String method;
    /** 请求类型 */
    @ApiModelProperty(value = "请求类型", position = 140)
    private Integer status;
    /* 请求开始时间戳 */
    @ApiModelProperty(value = "请求开始时间戳", position = 150)
    private Long start;
    /* 请求结束时间戳 */
    @ApiModelProperty(value = "请求结束时间戳", position = 160)
    private Long end;
    /* 耗时 */
    @ApiModelProperty(value = "耗时", position = 170)
    private Long consume;

}
