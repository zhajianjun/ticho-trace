package com.ticho.trace.server.infrastructure.entity;

import cn.easyes.annotation.HighLight;
import cn.easyes.annotation.IndexField;
import cn.easyes.annotation.IndexId;
import cn.easyes.annotation.IndexName;
import cn.easyes.annotation.rely.FieldType;
import cn.easyes.annotation.rely.IdType;
import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 日志收集信息
 *
 * @author zhajianjun
 * @date 2023-04-23 14:28
 */
@Data
@IndexName("log")
public class LogBO {

    /** id */
    @IndexId(type = IdType.CUSTOMIZE)
    private String id;
    /** 系统id */
    private String systemId;
    /** 链路id */
    private String traceId;
    /** 跨度id */
    private String spanId;
    /** 应用名称 */
    private String appName;
    /** 应用环境 */
    private String env;
    /** ip */
    private String ip;
    /** 上个链路的应用名称 */
    private String preAppName;
    /** 上个链路的Ip */
    private String preIp;
    /** 日志级别 */
    private String logLevel;
    /** 日志时间 */
    @IndexField(fieldType = FieldType.DATE, dateFormat = DatePattern.NORM_DATETIME_MS_PATTERN)
    @JSONField(format = DatePattern.NORM_DATETIME_MS_PATTERN)
    private LocalDateTime dateTime;
    /** 日志时间戳 */
    private Long dtTime;
    /** 类名称 */
    private String className;
    /** 方法名 */
    private String method;
    /** 序列号 */
    private Long seq;
    /** 内容 */
    @HighLight
    private String content;
    /** 线程名称 */
    private String threadName;
    /** 创建时间 */
    @IndexField(fieldType = FieldType.DATE, dateFormat = DatePattern.NORM_DATETIME_PATTERN)
    @JSONField(format = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;


}
