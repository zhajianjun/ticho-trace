package top.ticho.trace.common.constant;

/**
 * 日志静态常量
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
public class LogConst {

    private LogConst() {
    }

    /** 链路id key */
    public static final String TRACE_ID_KEY = "traceId";
    /** 跨度id key */
    public static final String SPAN_ID_KEY = "spanId";
    /** 当前应用名称 key */
    public static final String APP_NAME_KEY = "appName";
    /** 当前ip key */
    public static final String IP_KEY = "ip";
    /** 上个链路的应用名称 key */
    public static final String PRE_APP_NAME_KEY = "preAppName";
    /** 上个链路的Ip key */
    public static final String PRE_IP_KEY = "preIp";
    /** UNKNOWN */
    public static final String UNKNOWN = "UNKNOWN";
    /** TRACE_KEY */
    public static final String TRACE_KEY = "traceKey";
    /** TRACE */
    public static final String TRACE = "trace";
    /** 默认链路 */
    public static final String DEFAULT_TRACE = "[${traceId}].[${spanId}]";
    /** 耗时 key */
    public static final String MDC_KEY = "mdc";

    public static final String LOG_INDEX_PREFIX = "log";

    public static final String TRACE_INDEX_PREFIX = "trace";

}
