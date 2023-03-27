package top.ticho.trace.core.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import top.ticho.trace.core.push.TracePushContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 分布式日志appender
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
public class DistributedLogAppender extends AppenderBase<ILoggingEvent> {
    /** */
    private String url; // 日志推送的url
    /** 批量推送的日志数量，达到一定数量则进行推送日志 */
    private int pushSize = 100;
    /** 日志推送的时间间隔，单位：毫秒（ms） */
    private int flushInterval = 500;
    /** 日志缓冲队列 */
    private final BlockingQueue<ILoggingEvent> queue;
    /** 日志推送线程池 */
    private final ExecutorService executor;
    /** 日志序号，用于同一时刻日志的排序 */
    private final AtomicLong sequence;
    /** 上一次日志推送的时间戳 */
    private final AtomicLong pushTime;
    /** 上一次日志信息的时间戳 */
    private final AtomicLong lastLogTimeStamp;

    public DistributedLogAppender() {
        // @formatter:off
        this.queue = new LinkedBlockingQueue<>(10000);
        this.executor = new ThreadPoolExecutor(1, 1, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1), new ThreadPoolExecutor.DiscardOldestPolicy());
        this.sequence = new AtomicLong();
        this.pushTime = new AtomicLong();
        this.lastLogTimeStamp = new AtomicLong();
        // @formatter:on
    }


    public void setUrl(String url) {
        this.url = url;
    }

    public void setPushSize(int pushSize) {
        this.pushSize = pushSize;
    }

    public void setFlushInterval(int flushInterval) {
        this.flushInterval = flushInterval;
    }

    @Override
    public void start() {
        super.start();
        executor.execute(this::scheduleFlushTask);

    }

    @Override
    public void stop() {
        super.stop();
        shutdown();
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (event == null) {
            return;
        }
        queue.add(event);
    }

    public void scheduleFlushTask() {
        while (true) {
            try {
                int size = queue.size();
                long currentTimeMillis = System.currentTimeMillis();
                long time = currentTimeMillis - pushTime.get();
                // 日志达到推送的数量、或者达到一定时间间隔则推送日志
                if (size >= pushSize || time > flushInterval) {
                    List<ILoggingEvent> logs = new ArrayList<>();
                    queue.drainTo(logs, size);
                    if (!logs.isEmpty()) {
                        batchHandle(logs);
                        pushTime.set(currentTimeMillis);
                    }

                } else if (size == 0) {
                    // 当没有日志，则阻塞等待下一条日志产生，并进行推送
                    ILoggingEvent log = queue.take();
                    batchHandle(Collections.singletonList(log));
                    pushTime.set(currentTimeMillis);
                } else {
                    // 阻塞100ms
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                System.out.println("Failed to flush logs " + e.getMessage());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                }
            }
        }
    }


    private void batchHandle(List<ILoggingEvent> loggingEvents) {
        List<Map<String, String>> message = new ArrayList<>();
        for (ILoggingEvent event : loggingEvents) {
            long timeStamp = event.getTimeStamp();
            long lastLogTimeStampGet = lastLogTimeStamp.get();
            if (lastLogTimeStampGet == 0) {
                lastLogTimeStamp.set(timeStamp);
            }
            long currentSequence = 0;
            if (lastLogTimeStampGet == timeStamp) {
                currentSequence = sequence.incrementAndGet();
            } else {
                sequence.set(0);
                lastLogTimeStamp.set(timeStamp);
            }
            Map<String, String> mdc = new HashMap<>(event.getMDCPropertyMap());
            mdc.put("sequence", Long.toString(currentSequence));
            message.add(mdc);
        }
        TracePushContext.push(url, message);
    }

    private void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

}