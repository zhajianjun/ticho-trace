package top.ticho.trace.server.infrastructure.entity;

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
 * 链路信息DB对象
 *
 * @author zhajianjun
 * @date 2023-04-02 01:40:22
 */
@Data
@IndexName("trace")
public class TraceBO {

    /** id */
    @IndexId(type = IdType.CUSTOMIZE)
    private String id;
    /** 系统id */
    private String systemId;
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
    /** 方法 */
    private String method;
    /** 响应状态 */
    private Integer status;
    /* 请求开始时间戳 */
    private Long start;
    /* 请求结束时间戳 */
    private Long end;
    /* 请求开始时间 */
    @IndexField(fieldType = FieldType.DATE, dateFormat = DatePattern.NORM_DATETIME_MS_PATTERN)
    @JSONField(format = DatePattern.NORM_DATETIME_MS_PATTERN)
    private LocalDateTime startTime;
    /* 请求结束时间 */
    @IndexField(fieldType = FieldType.DATE, dateFormat = DatePattern.NORM_DATETIME_MS_PATTERN)
    @JSONField(format = DatePattern.NORM_DATETIME_MS_PATTERN)
    private LocalDateTime endTime;
    /* 耗时 */
    private Long consume;
    /** 创建时间 */
    @IndexField(fieldType = FieldType.DATE, dateFormat = DatePattern.NORM_DATETIME_PATTERN)
    @JSONField(format = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;

}
