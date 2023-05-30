package com.ticho.trace.core.handle;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.AntPathMatcher;
import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.ThreadUtil;
import com.ticho.trace.common.bean.LogInfo;
import com.ticho.trace.common.bean.TraceInfo;
import com.ticho.trace.common.constant.LogConst;
import com.ticho.trace.common.prop.TraceProperty;
import com.ticho.trace.core.util.OkHttpUtil;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 链路推送上下文
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
public class TracePushContext {
    private TracePushContext() {
    }

    /** url地址匹配 */
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();
    /** 线程池 */
    private static final ThreadPoolExecutor executor;

    static {
        // @formatter:off
        float blockingCoefficient = 0.8F;
        int poolSize = (int) (Runtime.getRuntime().availableProcessors() / (1 - blockingCoefficient));
        ThreadFactory threadFactory = ThreadUtil.newNamedThreadFactory(LogConst.THREAD_NAME_PREFIX_TRACE, false);
        executor = ExecutorBuilder.create()
            .setCorePoolSize(poolSize)
            .setMaxPoolSize(poolSize)
            .setKeepAliveTime(0L)
            .setThreadFactory(threadFactory)
            .build();
        // @formatter:on
    }

    /**
     * 推送日志信息
     *
     * @param url      url
     * @param logInfos 日志信息
     */
    public static void pushLogInfo(String url, String secret, List<LogInfo> logInfos) {
        OkHttpUtil.push(url, secret, logInfos);
    }

    /**
     * 异步推送链路信息
     *
     * @param traceInfo 跟踪信息
     */
    public static void asyncPushTrace(TraceProperty traceProperty, TraceInfo traceInfo) {
        executor.execute(() -> pushTrace(traceProperty, traceInfo));
    }

    /**
     * 推送链路信息
     *
     * @param traceInfo 跟踪信息
     */
    public static void pushTrace(TraceProperty traceProperty, TraceInfo traceInfo) {
        String traceUrl = traceProperty.getUrl();
        String secret = traceProperty.getSecret();
        // 配置不推送链路信息
        if (!traceProperty.getPushTrace()) {
            return;
        }
        // 不推送链路信息的url匹配
        List<String> antPatterns = traceProperty.getAntPatterns();
        if (CollUtil.isEmpty(antPatterns)) {
            OkHttpUtil.push(traceUrl, secret, traceInfo);
        }
        String url = traceInfo.getUrl();
        boolean anyMatch = antPatterns.stream().anyMatch(x -> antPathMatcher.match(x, url));
        boolean isFirstSpanId = Objects.equals(LogConst.FIRST_SPAN_ID, traceInfo.getSpanId());
        // 如果匹配到的url是根节点则不推送链路信息
        if (anyMatch && isFirstSpanId) {
            return;
        }
        OkHttpUtil.push(traceUrl, secret, traceInfo);
    }

}
