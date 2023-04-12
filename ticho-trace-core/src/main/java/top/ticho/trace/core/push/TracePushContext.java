package top.ticho.trace.core.push;

import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.ThreadUtil;
import top.ticho.trace.common.bean.LogInfo;
import top.ticho.trace.common.bean.TraceInfo;
import top.ticho.trace.common.constant.LogConst;
import top.ticho.trace.common.prop.TraceLogProperty;
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
    /** 日志推送适配器 */
    private static PushAdapter TRACE_PUSH_ADAPTER;
    private static TraceLogProperty traceLogProperty;

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
        // TODO 项目初始化时，如果控制链路、日志是否打印
        traceLogProperty = new TraceLogProperty();
        // @formatter:on
    }

    public static void setTracePushAdapter(PushAdapter pushAdapter) {
        TRACE_PUSH_ADAPTER = pushAdapter;
    }

    public static void setTraceLogProperty(TraceLogProperty traceLogProperty) {
        TracePushContext.traceLogProperty = traceLogProperty;
    }


    /**
     * 推送日志信息
     *
     * @param url url
     * @param logInfos 日志信息
     */
    public static void pushLogInfo(String url, List<LogInfo> logInfos) {
        if (!traceLogProperty.getIsPushLog()) {
            return;
        }
        TRACE_PUSH_ADAPTER.push(url, logInfos);
    }

    /**
     * 推送链路信息
     *
     * @param url url
     * @param traceInfo 跟踪信息
     */
    public static void pushTraceInfo(String url, TraceInfo traceInfo) {
        if (!traceLogProperty.getIsPushTrace()) {
            return;
        }
        TRACE_PUSH_ADAPTER.push(url, traceInfo);
    }

    /**
     * 异步推送跟踪信息
     *
     * @param url url
     * @param traceInfo 跟踪信息
     */
    public static void pushTraceInfoAsync(String url, TraceInfo traceInfo) {
        if (!traceLogProperty.getIsPushTrace()) {
            return;
        }
        executor.execute(() -> TRACE_PUSH_ADAPTER.push(url, traceInfo));
    }


}
