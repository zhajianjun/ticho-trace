package top.ticho.trace.core.handle;

import cn.hutool.core.date.SystemClock;
import com.lmax.disruptor.EventHandler;
import top.ticho.trace.common.bean.LogInfo;
import top.ticho.trace.core.push.TracePushContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 日志事件消费
 *
 * @author zhajianjun
 * @date 2023-04-10 09:30
 */
public class LogEventConsumer implements EventHandler<LogInfo> {
    // 推送地址
    private final String url;
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

    public LogEventConsumer(String url, int batchSize, long flushInterval) {
        this.url = url;
        this.batchSize = batchSize;
        this.flushInterval = flushInterval;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * 事件处理
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

    private synchronized void execute() {
        if (!logInfos.isEmpty()) {
            TracePushContext.push(url, logInfos);
            // 清空批次缓存
            logInfos.clear();
            lastTime = SystemClock.now();
        }
        taskExecute.set(false);
    }

    public void start() {
        executorService.scheduleWithFixedDelay(() -> {
            if (!taskExecute.get()) {
                return;
            }
            execute();
        }, 500, flushInterval, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
