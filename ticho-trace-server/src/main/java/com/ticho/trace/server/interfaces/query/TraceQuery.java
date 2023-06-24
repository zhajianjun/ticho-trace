package com.ticho.trace.server.interfaces.query;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ticho.boot.view.core.BasePageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 链路信息Query
 *
 * @author zhajianjun
 * @date 2023-06-10 11:05
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "链路信息Query")
public class TraceQuery extends BasePageQuery {

    /** 主键编号 */
    @ApiModelProperty(value = "主键编号", position = 10)
    private Long id;
    /** 系统id */
    @NotBlank(message = "系统id不能为空")
    @ApiModelProperty(value = "系统id", position = 20)
    private String systemId;
    /** 链路id */
    @ApiModelProperty(value = "链路id", position = 30)
    private String traceId;
    /** 跨度id */
    @ApiModelProperty(value = "跨度id", position = 40)
    private String spanId;
    /** 应用名称 */
    @ApiModelProperty(value = "应用名称", position = 50)
    private String appName;
    /** 应用环境 */
    @ApiModelProperty(value = "应用环境", position = 60)
    private String env;
    /** ip */
    @ApiModelProperty(value = "ip", position = 70)
    private String ip;
    /** 上个链路的应用名称 */
    @ApiModelProperty(value = "上个链路的应用名称", position = 80)
    private String preAppName;
    /** 上个链路的Ip */
    @ApiModelProperty(value = "上个链路的Ip", position = 90)
    private String preIp;
    /** 请求类型 */
    @ApiModelProperty(value = "请求类型", position = 100)
    private String type;
    /** 接口 */
    @ApiModelProperty(value = "接口", position = 110)
    private String url;
    /** 端口号 */
    @ApiModelProperty(value = "端口号", position = 120)
    private String port;
    /** 全路径接口 */
    @ApiModelProperty(value = "全路径接口", position = 130)
    private String fullUrl;
    /** 方法 */
    @ApiModelProperty(value = "方法", position = 140)
    private String method;
    /** 请求类型 */
    @ApiModelProperty(value = "响应状态", position = 150)
    private Integer status;
    /* 请求开始时间起始 */
    @NotNull(message = "请求开始时间起始不能为空")
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_MS_PATTERN, timezone = "GMT+8")
    @ApiModelProperty(value = "请求开始时间起始", position = 160)
    private LocalDateTime startTimeFirst;
    /* 请求开始时间终止 */
    @NotNull(message = "请求开始时间终止不能为空")
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_MS_PATTERN, timezone = "GMT+8")
    @ApiModelProperty(value = "请求开始时间终止", position = 170)
    private LocalDateTime startTimeLast;
    /* 请求结束时间起始 */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_MS_PATTERN, timezone = "GMT+8")
    @ApiModelProperty(value = "请求结束时间起始", position = 180)
    private LocalDateTime endTimeFirst;
    /* 请求结束时间终止 */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_MS_PATTERN, timezone = "GMT+8")
    @ApiModelProperty(value = "请求结束时间终止", position = 190)
    private LocalDateTime endTimeLast;
    /* 耗时起始 */
    @ApiModelProperty(value = "耗时起始", position = 200)
    private Long consumeFirst;
    /* 耗时终止 */
    @ApiModelProperty(value = "耗时终止", position = 210)
    private Long consumeLast;

}
