package top.ticho.trace.core.constant;

import cn.hutool.core.net.Ipv4Util;
import sun.net.util.IPAddressUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
    public static final String CURR_APP_NAME_KEY = "currAppName";
    /** 当前ip key */
    public static final String CURR_IP_KEY = "currIp";
    /** 上个链路的应用名称 key */
    public static final String PRE_APP_NAME_KEY = "preAppName";
    /** 上个链路的Ip key */
    public static final String PRE_IP_KEY = "preIp";
    /** UNKNOWN */
    public static final String UNKNOWN = "UNKNOWN";
    /** MDC_KEY */
    public static final String MDC_KEY = "ticho";


    public static void main(String[] args) throws UnknownHostException {

    }

}
