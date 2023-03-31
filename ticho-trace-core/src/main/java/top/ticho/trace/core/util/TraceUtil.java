package top.ticho.trace.core.util;

import cn.hutool.core.lang.generator.SnowflakeGenerator;
import cn.hutool.core.util.StrUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import org.slf4j.MDC;
import top.ticho.trace.core.constant.LogConst;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
public class TraceUtil {

    private TraceUtil() {
    }

    /** 下个跨度id的索引 */
    private static final TransmittableThreadLocal<AtomicInteger> NEXT_SPAN_INDEX_TL = new TransmittableThreadLocal<>();

    public static final SnowflakeGenerator SNOW = new SnowflakeGenerator();

    public static String nextSpanId() {
        String currentSpanId = MDC.get(LogConst.SPAN_ID_KEY);
        int currentSpanIndex = NEXT_SPAN_INDEX_TL.get().incrementAndGet();
        return StrUtil.format("{}.{}", currentSpanId, currentSpanIndex);
    }

    public static void prepare(Map<String, String> map) {
        // 链路id */
        String traceId = nullDefault(map.get(LogConst.TRACE_ID_KEY), ()-> Long.toString(SNOW.next()));
        // 跨度id */
        String spanId = nullDefault(map.get(LogConst.SPAN_ID_KEY), ()-> null);
        // 当前应用名称 */
        String currAppName = nullDefault(map.get(LogConst.CURR_APP_NAME_KEY));
        // 当前ip */
        String currIp = nullDefault(map.get(LogConst.CURR_IP_KEY));
        // 上个链路的应用名称 */
        String preAppName = nullDefault(map.get(LogConst.PRE_APP_NAME_KEY));
        // 上个链路的Ip */
        String preIp = nullDefault(map.get(LogConst.PRE_IP_KEY));
        MDC.put(LogConst.MDC_KEY, "");
        MDC.put(LogConst.TRACE_ID_KEY, traceId);
        if (StrUtil.isBlank(spanId)) {
            spanId = "0";
        }
        NEXT_SPAN_INDEX_TL.set(new AtomicInteger(0));
        MDC.put(LogConst.SPAN_ID_KEY, spanId);
        MDC.put(LogConst.CURR_IP_KEY, currIp);
        MDC.put(LogConst.CURR_APP_NAME_KEY, currAppName);
        MDC.put(LogConst.PRE_IP_KEY, preIp);
        MDC.put(LogConst.PRE_APP_NAME_KEY, preAppName);
    }

    public static void complete(){
        //移除MDC里的信息
        MDC.remove(LogConst.MDC_KEY);
        MDC.remove(LogConst.TRACE_ID_KEY);
        MDC.remove(LogConst.SPAN_ID_KEY);
        MDC.remove(LogConst.CURR_APP_NAME_KEY);
        MDC.remove(LogConst.CURR_IP_KEY);
        MDC.remove(LogConst.PRE_APP_NAME_KEY);
        MDC.remove(LogConst.PRE_IP_KEY);
    }

    public static String nullDefault(String obj) {
        return nullDefault(obj, ()-> LogConst.UNKNOWN);
    }

    public static String nullDefault(String obj, Supplier<String> supplier) {
        return Optional.ofNullable(obj).filter(StrUtil::isNotBlank).orElseGet(supplier);
    }


}
