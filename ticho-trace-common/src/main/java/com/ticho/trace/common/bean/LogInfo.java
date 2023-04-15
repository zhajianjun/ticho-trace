package com.ticho.trace.common.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 日志收集日志
 *
 * @author zhajianjun
 * @date 2023-03-30 20:20:20
 */
@NoArgsConstructor
@Data
public class LogInfo {

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

    /** 日志级别 */
    private String logLevel;

    /** 日志时间 */
    private String dateTime;

    /** 日志时间戳 */
    private Long dtTime;

    /** 类名称 */
    private String className;

    /** 方法名 */
    private String method;

    /** 序列号 */
    private Long seq;

    /** 内容 */
    private String content;

    /** 线程名称 */
    private String threadName;

    /** mdc信息 */
    private Map<String, String> mdc;

}
