package top.ticho.trace.server.entity;

import cn.easyes.annotation.IndexId;
import cn.easyes.annotation.rely.IdType;
import lombok.Data;

/**
 * @author zhajianjun
 * @date 2023-04-02 01:40:22
 */
@Data
public class TraceInfo {

    /** id */
    @IndexId(type = IdType.CUSTOMIZE)
    private String id;

    /** 链路id */
    private String traceId;

    /** 跨度id */
    private String spanId;

    /** 当前应用名称 */
    private String appName;

    /** 当前ip */
    private String ip;

    /** 上个链路的应用名称 */
    private String preAppName;

    /** 上个链路的Ip */
    private String preIp;

    /** 请求类型 */
    private String type;

    /** 接口 */
    private String url;

    /** 端口号 */
    private String port;

    /** 全路径接口 */
    private String fullUrl;

    /** 接口 */
    private String method;

    /** 请求类型 */
    private Integer status;

    /* 请求开始时间戳 */
    private Long start;

    /* 请求结束时间戳 */
    private Long end;

    /* 请求开始时间 */
    private String startTime;

    /* 请求结束时间 */
    private String endTime;

    /* 耗时 */
    private Long consume;

}
