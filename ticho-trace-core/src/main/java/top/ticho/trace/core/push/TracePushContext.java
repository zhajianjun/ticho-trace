package top.ticho.trace.core.push;

import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.ThreadUtil;
import top.ticho.trace.common.bean.LogInfo;
import top.ticho.trace.common.bean.TraceInfo;
import top.ticho.trace.common.constant.LogConst;
import top.ticho.trace.core.push.adapter.OkHttpPushAdapter;

import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
public class TracePushContext {
    private TracePushContext() {
    }
    /** 线程池 */
    private static final ThreadPoolExecutor executor;
    /**  */
    private static PushAdapter TRACE_PUSH_ADAPTER;

    static {
        // @formatter:off
        float blockingCoefficient = 0.8F;
        int poolSize = (int) (Runtime.getRuntime().availableProcessors() / (1 - blockingCoefficient));
        ThreadFactory threadFactory = ThreadUtil.newNamedThreadFactory(LogConst.THREAD_NAME_PREFIX_LOG, false);
        executor = ExecutorBuilder.create()
            .setCorePoolSize(poolSize)
            .setMaxPoolSize(poolSize)
            .setKeepAliveTime(0L)
            .setThreadFactory(threadFactory)
            .build();
        TRACE_PUSH_ADAPTER = new OkHttpPushAdapter();
        // @formatter:on
    }

    public static void setTracePushAdapter(PushAdapter pushAdapter) {
        TRACE_PUSH_ADAPTER = pushAdapter;
    }

    public static void pushLogInfo(String url, List<LogInfo> logInfos) {
        TRACE_PUSH_ADAPTER.push(url, logInfos);
    }

    public static void pushTraceInfo(String url, TraceInfo traceInfo) {
        TRACE_PUSH_ADAPTER.push(url, traceInfo);
    }

    public static void pushTraceInfoAsync(String url, TraceInfo traceInfo) {
        executor.execute(() -> TRACE_PUSH_ADAPTER.push(url, traceInfo));
    }


}
