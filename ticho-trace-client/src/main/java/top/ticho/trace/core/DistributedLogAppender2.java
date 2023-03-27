package top.ticho.trace.core;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.ticho.boot.json.util.JsonUtil;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.ArrayList;
import java.util.Collections;
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
 * @date 2023-03-19 18:32
 */
public class DistributedLogAppender2 extends AppenderBase<ILoggingEvent> {
    /** */
    private String url; // 日志推送的url
    /** 批量推送的日志数量，达到一定数量则进行推送日志 */
    private int pushSize = 100;
    /** 日志推送的时间间隔，单位：毫秒（ms） */
    private int flushInterval = 500;
    /** http客户端 */
    private final OkHttpClient httpClient;
    /** 日志缓冲队列 */
    private final BlockingQueue<String> queue;
    /** 日志推送线程池 */
    private final ExecutorService executor;
    /** 日志序号，用于同一时刻日志的排序 */
    private final AtomicLong sequence;
    /** 上一次日志推送的时间戳 */
    private final AtomicLong pushTime;
    /** 上一次日志信息的时间戳 */
    private final AtomicLong lastLogTimeStamp;

    public DistributedLogAppender2() {
        this.queue = new LinkedBlockingQueue<>(10000);
        this.httpClient = new OkHttpClient.Builder().build();
        this.executor = new ThreadPoolExecutor(1, 1, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1), new ThreadPoolExecutor.DiscardOldestPolicy());
        this.sequence = new AtomicLong();
        this.pushTime = new AtomicLong();
        this.lastLogTimeStamp = new AtomicLong();
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
        Map<String, String> mdc = event.getMDCPropertyMap();
        mdc.put("sequence", Long.toString(currentSequence));
        String e = JsonUtil.toJsonString(mdc);
        System.out.println("日志：" + e);
        queue.add(e);
    }

    public void scheduleFlushTask() {
        while (true) {
            try {
                int size = queue.size();
                long currentTimeMillis = System.currentTimeMillis();
                long time = currentTimeMillis - pushTime.get();
                // 日志达到推送的数量、或者达到一定时间间隔则推送日志
                if (size >= pushSize || time > flushInterval) {
                    List<String> logs = new ArrayList<>();
                    queue.drainTo(logs, size);
                    batchHandle(logs);
                    pushTime.set(currentTimeMillis);
                } else if (size == 0) {
                    // String，则阻塞等待下一条日志产生，并进行推送
                    String log = queue.take();
                    batchHandle(Collections.singletonList(log));
                    pushTime.set(currentTimeMillis);
                } else {
                    // 阻塞100ms
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                String exMsg=e.getMessage();
                System.out.println("Failed to flush logs " + e.getMessage());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                }
            }
        }
    }


    private void batchHandle(List<String> messages) {
        String str = JsonUtil.toJsonString(messages);
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), str);
            Request request = new Request.Builder().url(url).post(requestBody).build();
            httpClient.newCall(request).execute();
        } catch (Exception e) {
            System.out.println("push error:----------------" + e.getMessage() + "-------------------");
        }

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