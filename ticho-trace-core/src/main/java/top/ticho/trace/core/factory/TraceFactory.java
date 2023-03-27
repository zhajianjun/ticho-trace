package top.ticho.trace.core.factory;

import cn.hutool.core.util.StrUtil;
import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
public class TraceFactory {

    private TraceFactory() {
    }

    /** 链路id */
    private static final TransmittableThreadLocal<String> TRACE_ID_TL = new TransmittableThreadLocal<>();
    /** 跨度id */
    private static final TransmittableThreadLocal<String> CURR_SPAN_ID_TL = new TransmittableThreadLocal<>();
    /** 下个跨度id的索引 */
    private static final TransmittableThreadLocal<AtomicInteger> NEXT_SPAN_INDEX_TL = new TransmittableThreadLocal<>();
    /** 当前应用名称 */
    private static final TransmittableThreadLocal<String> CURR_APP_NAME_TL = new TransmittableThreadLocal<>();
    /** 当前ip */
    private static final TransmittableThreadLocal<String> CURR_IP_TL = new TransmittableThreadLocal<>();
    /** 上个链路的应用名称 */
    private static final TransmittableThreadLocal<String> PRE_APP_NAME_TL = new TransmittableThreadLocal<>();
    /** 上个链路的Ip */
    private static final TransmittableThreadLocal<String> PRE_IP_TL = new TransmittableThreadLocal<>();

    public static void setTraceId(String traceId) {
        TRACE_ID_TL.set(traceId);
    }

    public static String getTraceId() {
        return TRACE_ID_TL.get();
    }

    public static void removeTraceId() {
        TRACE_ID_TL.remove();
    }

    public static void setSpanId(String spanId) {
        if (StrUtil.isBlank(spanId)) {
            spanId = "0";
        }
        CURR_SPAN_ID_TL.set(spanId);
        NEXT_SPAN_INDEX_TL.set(new AtomicInteger(0));
    }

    public static String getSpanId() {
        return CURR_SPAN_ID_TL.get();
    }

    public static String createNextSpanId() {
        String currentSpanId = CURR_SPAN_ID_TL.get();
        int currentSpanIndex = NEXT_SPAN_INDEX_TL.get().incrementAndGet();
        return StrUtil.format("{}.{}", currentSpanId, currentSpanIndex);
    }

    public static void removeSpanId() {
        CURR_SPAN_ID_TL.remove();
    }

    public static void setNextSpanIndex(AtomicInteger atomicInteger) {
        NEXT_SPAN_INDEX_TL.set(atomicInteger);
    }

    public static AtomicInteger getNextSpanIndex() {
        return NEXT_SPAN_INDEX_TL.get();
    }

    public static void removeNextSpanIndex() {
        NEXT_SPAN_INDEX_TL.remove();
    }

    public static void setCurrAppName(String currIp) {
        CURR_APP_NAME_TL.set(currIp);
    }

    public static String getCurrAppName() {
        return CURR_APP_NAME_TL.get();
    }

    public static void removeCurrAppName() {
        CURR_APP_NAME_TL.remove();
    }

    public static void setCurrIp(String currIp) {
        CURR_IP_TL.set(currIp);
    }

    public static String getCurrIp() {
        return CURR_IP_TL.get();
    }

    public static void removeCurrIp() {
        CURR_IP_TL.remove();
    }

    public static void setPreAppName(String currIp) {
        PRE_APP_NAME_TL.set(currIp);
    }

    public static String getPreAppName() {
        return PRE_APP_NAME_TL.get();
    }

    public static void removePreAppName() {
        PRE_APP_NAME_TL.remove();
    }

    public static void setPreIp(String currIp) {
        PRE_IP_TL.set(currIp);
    }

    public static String getPreIp() {
        return PRE_IP_TL.get();
    }

    public static void removePreIp() {
        PRE_IP_TL.remove();
    }


}
