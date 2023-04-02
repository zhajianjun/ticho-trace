package top.ticho.trace.spring.interceptor;

import cn.hutool.http.useragent.UserAgent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.method.HandlerMethod;

import java.time.LocalDateTime;

/**
 * 接口日志
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
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

    /** 请求类型 */
    private String type;

    /** 请求地址 */
    private String url;

    /** 端口号 */
    private String port;

    /** 请求参数 */
    private String reqParams;

    /** 请求体 */
    private String reqBody;

    /** 请求头 */
    private String reqHeaders;

    /** 响应体 */
    private String resBody;

    /** 响应头 */
    private String resHeaders;

    /* 请求开始时间戳 */
    private Long start;

    /* 请求结束时间戳 */
    private Long end;

    /* 请求开始时间 */
    private LocalDateTime startTime;

    /* 请求结束时间 */
    private LocalDateTime endTime;

    /* 请求间隔 */
    private Long consume;

    /* 用户信息 */
    private String username;

    /* User-Agent信息对象 */
    @JsonIgnore
    private UserAgent userAgent;

    @JsonIgnore
    private HandlerMethod handlerMethod;

    public Long getConsume() {
        if (start == null || end == null) {
            return 0L;
        }
        return end - start;
    }
}
