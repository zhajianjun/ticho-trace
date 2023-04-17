package com.ticho.trace.common.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * 链路收集信息
 *
 * @author zhajianjun
 * @date 2023-03-30 20:20:20
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TraceInfo {

    /** 链路id */
    private String traceId;
    /** 跨度id */
    private String spanId;
    /** 当前应用名称 */
    private String appName;
    /** 当前应用环境 */
    private String env;
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
    /** 响应状态 */
    private Integer status;
    /* 请求开始时间戳 */
    private Long start;
    /* 请求结束时间戳 */
    private Long end;
    /* 耗时 */
    private Long consume;


    public String getFullUrl() {
        if (url == null || port == null || ip == null) {
            return null;
        }
        return String.format("%s:%s%s", ip, port, url);
    }

}
