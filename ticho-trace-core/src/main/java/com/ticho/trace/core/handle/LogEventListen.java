package com.ticho.trace.core.handle;

import cn.hutool.core.date.SystemClock;
import com.lmax.disruptor.EventHandler;
import com.ticho.trace.common.bean.LogInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 日志事件消费者
 *
 * @author zhajianjun
 * @date 2023-04-10 09:30
 */
public class LogEventListen implements EventHandler<LogInfo> {
    // 推送地址
    private final String url;
    /** 日志推送的秘钥 */
    private final String secret;
    // 批次数据缓存
    private final List<LogInfo> logInfos = new ArrayList<>();
    // 批次大小
    private final int batchSize;
    // 定时任务间隔时间
    private final long flushInterval;
    // 上次消费时间
    private long lastTime = System.currentTimeMillis();
    // 定时任务
    private final ScheduledExecutorService executorService;
    // 定时任务逻辑是否执行
    private final AtomicBoolean taskExecute = new AtomicBoolean(false);

    public LogEventListen(String url, String secret, int batchSize, long flushInterval, ThreadFactory threadFactory) {
        this.url = url;
        this.secret = secret;
        this.batchSize = batchSize;
        this.flushInterval = flushInterval;
        this.executorService = Executors.newSingleThreadScheduledExecutor(threadFactory);
    }

    /**
     * 日志事件消费
     *
     * @param event 事件
     * @param sequence 序列
     * @param endOfBatch 是否达到批次大小或者到达批次末尾
     */
    public void onEvent(LogInfo event, long sequence, boolean endOfBatch) {
        // 添加数据到批次缓存中
        logInfos.add(event);
        long currentTime = SystemClock.now();
        // 数量达到批次大小或者距离上次处理时间大于 flushInterval时，执行处理逻辑
        if (logInfos.size() >= batchSize || currentTime - lastTime >= flushInterval) {
            execute();
            return;
        }
        taskExecute.set(true);
    }

    /**
     * 日志处理
     */
    private synchronized void execute() {
        if (!logInfos.isEmpty()) {
            TracePushContext.pushLogInfo(url, secret, logInfos);
            // 清空批次缓存
            logInfos.clear();
            lastTime = SystemClock.now();
        }
        taskExecute.set(false);
    }

    /**
     * 开启定时任务
     */
    public void start() {
        executorService.scheduleWithFixedDelay(() -> {
            if (!taskExecute.get()) {
                return;
            }
            execute();
        }, 500, flushInterval, TimeUnit.MILLISECONDS);
    }

    /**
     * 关闭定时任务
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
