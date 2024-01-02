package top.ticho.trace.core.util;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import top.ticho.trace.common.bean.TraceInit;
import top.ticho.trace.common.constant.LogConst;
import org.slf4j.MDC;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * 链路工具类
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
public class TraceUtil {

    private TraceUtil() {
    }

    /** 下个跨度id的索引 */
    private static final TransmittableThreadLocal<AtomicInteger> NEXT_SPAN_INDEX_TL = new TransmittableThreadLocal<>();


    public static AtomicInteger getNextSpanIndex() {
        return NEXT_SPAN_INDEX_TL.get();
    }

    public static void setNextSpanIndex(AtomicInteger atomicInteger) {
        NEXT_SPAN_INDEX_TL.set(atomicInteger);
    }

    public static void clearNextSpanIndex() {
        NEXT_SPAN_INDEX_TL.remove();
    }

    /**
     * 下一个跨度id
     *
     * @return {@link String}
     */
    public static String nextSpanId() {
        String currentSpanId = MDC.get(LogConst.SPAN_ID_KEY);
        int currentSpanIndex = NEXT_SPAN_INDEX_TL.get().incrementAndGet();
        return StrUtil.format("{}.{}", currentSpanId, currentSpanIndex);
    }

    /**
     * 链路生成准备
     *
     * @param map map
     */
    public static void prepare(Map<String, String> map) {
        // 链路id */
        String traceId = map.get(LogConst.TRACE_ID_KEY);
        // 跨度id */
        String spanId = map.get(LogConst.SPAN_ID_KEY);
        // 当前应用名称 */
        String currAppName = map.get(LogConst.APP_NAME_KEY);
        // 当前ip */
        String currIp = map.get(LogConst.IP_KEY);
        // 上个链路的应用名称 */
        String preAppName = map.get(LogConst.PRE_APP_NAME_KEY);
        // 上个链路的Ip */
        String preIp = map.get(LogConst.PRE_IP_KEY);
        // 链路
        String trace = map.get(LogConst.TRACE_KEY);
        prepare(traceId, spanId, currAppName, currIp, preAppName, preIp, trace);
    }

    /**
     * 链路生成准备
     *
     * @param traceInit 跟踪初始化
     */
    public static void prepare(TraceInit traceInit) {
        String traceId = traceInit.getTraceId();
        String spanId = traceInit.getSpanId();
        String appName = traceInit.getAppName();
        String ip = traceInit.getIp();
        String preAppName = traceInit.getPreAppName();
        String preIp = traceInit.getPreIp();
        String trace = traceInit.getTrace();
        prepare(traceId, spanId, appName, ip, preAppName, preIp, trace);
    }

    /**
     * 链路生成准备
     *
     * @param traceId    链路id
     * @param spanId     跨度id
     * @param appName    当前应用名称
     * @param ip         当前ip
     * @param preAppName 上个链路的应用名称
     * @param preIp      上个链路的ip
     * @param trace      链路
     */
    public static void prepare(String traceId, String spanId, String appName, String ip, String preAppName, String preIp, String trace) {
        if (StrUtil.isBlank(traceId)) {
            traceId = IdUtil.getSnowflakeNextIdStr();
            spanId = LogConst.FIRST_SPAN_ID;
        } else {
            spanId = nullDefault(spanId, () -> LogConst.FIRST_SPAN_ID);
        }
        NEXT_SPAN_INDEX_TL.set(new AtomicInteger(0));
        appName = nullDefault(appName);
        ip = nullDefault(ip);
        preAppName = nullDefault(preAppName);
        preIp = nullDefault(preIp);
        trace = nullDefault(trace, () -> LogConst.DEFAULT_TRACE);
        MDC.put(LogConst.TRACE_KEY, trace);
        MDC.put(LogConst.TRACE_ID_KEY, traceId);
        MDC.put(LogConst.SPAN_ID_KEY, spanId);
        MDC.put(LogConst.IP_KEY, ip);
        MDC.put(LogConst.APP_NAME_KEY, appName);
        MDC.put(LogConst.PRE_IP_KEY, preIp);
        MDC.put(LogConst.PRE_APP_NAME_KEY, preAppName);
        render();
    }

    /**
     * 链路完成时处理
     */
    public static void complete() {
        // 移除MDC里的信息
        MDC.remove(LogConst.TRACE_KEY);
        MDC.remove(LogConst.TRACE_ID_KEY);
        MDC.remove(LogConst.SPAN_ID_KEY);
        MDC.remove(LogConst.APP_NAME_KEY);
        MDC.remove(LogConst.IP_KEY);
        MDC.remove(LogConst.PRE_APP_NAME_KEY);
        MDC.remove(LogConst.PRE_IP_KEY);
        NEXT_SPAN_INDEX_TL.remove();
    }

    /**
     * 更新trace表达式
     */
    public static void putTrace(String trace) {
        trace = nullDefault(trace, () -> Optional.ofNullable(MDC.get(LogConst.TRACE_KEY)).orElse(LogConst.DEFAULT_TRACE));
        MDC.put(LogConst.TRACE_KEY, trace);
    }

    /**
     * 渲染trace表达式
     */
    public static void render() {
        String traceKey = MDC.get(LogConst.TRACE_KEY);
        String trace = BeetlUtil.render(traceKey, MDC.getCopyOfContextMap());
        MDC.put(LogConst.TRACE_KEY, trace);
    }

    /**
     * 空值返回处理
     *
     * @param obj obj
     * @return {@link String}
     */
    public static String nullDefault(String obj) {
        return nullDefault(obj, () -> LogConst.UNKNOWN);
    }

    /**
     * 空值返回处理
     *
     * @param obj      obj
     * @param supplier 供应商
     * @return {@link String}
     */
    public static String nullDefault(String obj, Supplier<String> supplier) {
        return Optional.ofNullable(obj).filter(StrUtil::isNotBlank).orElseGet(supplier);
    }

}
