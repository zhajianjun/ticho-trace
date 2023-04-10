package top.ticho.trace.core.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import cn.hutool.core.thread.ThreadUtil;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.TimeoutBlockingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import top.ticho.trace.common.bean.LogInfo;
import top.ticho.trace.common.constant.LogConst;
import top.ticho.trace.core.handle.LogEventConsumer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 分布式日志appender
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
public class DistributedLogAppender extends AppenderBase<ILoggingEvent> {
    /** 应用名称 */
    private String appName = LogConst.UNKNOWN;
    /** 日志推送的url */
    private String url;
    /** 批量推送的日志数量，达到一定数量则进行推送日志 */
    private int pushSize = 100;
    /** 日志推送的时间间隔，单位：毫秒（ms） */
    private int flushInterval = 500;
    /** 日志序号，用于同一时刻日志的排序 */
    private final AtomicLong sequence;
    /** 上一次日志信息的时间戳 */
    private final AtomicLong lastLogTimeStamp;

    private Disruptor<LogInfo> disruptor;
    private RingBuffer<LogInfo> ringBuffer;
    private LogEventConsumer logEventConsumer;


    public DistributedLogAppender() {
        // @formatter:off
        this.sequence = new AtomicLong();
        this.lastLogTimeStamp = new AtomicLong();
        // @formatter:on
    }


    public void setUrl(String url) {
        this.url = url;
    }

    public void setAppName(String appName) {
        this.appName = appName;
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
        // 环形缓冲区大小，必须是2的幂次方
        int bufferSize = 1024;
        // 等待策略，超时
        WaitStrategy waitStrategy = new TimeoutBlockingWaitStrategy(30, TimeUnit.SECONDS);
        // 线程工厂
        ThreadFactory threadFactory = ThreadUtil.newNamedThreadFactory("ticho-trace", false);
        // 事件工厂
        EventFactory<LogInfo> eventFactory = LogInfo::new;
        // 生产者类型，单个还是多个
        ProducerType producerType = ProducerType.MULTI;
        this.disruptor = new Disruptor<>(eventFactory, bufferSize, threadFactory, producerType, waitStrategy);
        this.logEventConsumer = new LogEventConsumer(url, pushSize, flushInterval);
        // 注册事件消费者
        disruptor.handleEventsWith(logEventConsumer);
        // 启动Disruptor
        this.ringBuffer = disruptor.start();
        logEventConsumer.start();
    }

    @Override
    public void stop() {
        super.stop();
        disruptor.shutdown();
        logEventConsumer.shutdown();
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (event == null) {
            return;
        }
        // 获取下一个序号
        long ringSeq = ringBuffer.next();
        // 根据序号创建数据
        LogInfo logInfo = ringBuffer.get(ringSeq);
        // 日志 时间戳
        long timeStamp = event.getTimeStamp();
        // 上一次日志信息的时间戳为空，则默认为当前日志时间戳
        long lastLogTimeStampGet = lastLogTimeStamp.get();
        if (lastLogTimeStampGet == 0) {
            lastLogTimeStamp.set(timeStamp);
        }
        // 默认日志序列为0
        long currentSequence = 0;
        // 上一次日志信息的时间戳和日志时间戳相等，序列增加；否则，重置序列为0，并更新上一次日志信息的时间戳
        if (lastLogTimeStampGet == timeStamp) {
            currentSequence = sequence.incrementAndGet();
        } else {
            sequence.set(0);
            lastLogTimeStamp.set(timeStamp);
        }
        Map<String, String> mdcMap = new HashMap<>(event.getMDCPropertyMap());
        String appName = Optional.ofNullable(mdcMap.get(LogConst.APP_NAME_KEY)).orElse(this.appName);
        logInfo.setTraceId(mdcMap.get(LogConst.TRACE_ID_KEY));
        logInfo.setSpanId(mdcMap.get(LogConst.SPAN_ID_KEY));
        logInfo.setAppName(appName);
        logInfo.setIp(mdcMap.get(LogConst.IP_KEY));
        logInfo.setPreAppName(mdcMap.get(LogConst.PRE_APP_NAME_KEY));
        logInfo.setPreIp(mdcMap.get(LogConst.PRE_IP_KEY));
        logInfo.setLogLevel(event.getLevel().toString());
        logInfo.setDtTime(lastLogTimeStampGet);
        logInfo.setClassName(event.getLoggerName());
        logInfo.setSeq(currentSequence);
        logInfo.setContent(event.getFormattedMessage());
        logInfo.setThreadName(event.getThreadName());
        logInfo.setMdc(mdcMap);
        StackTraceElement[] stackTraceElements = event.getCallerData();
        if (stackTraceElements != null && stackTraceElements.length > 0) {
            StackTraceElement stackTraceElement = stackTraceElements[0];
            String method = stackTraceElement.getMethodName();
            String line = String.valueOf(stackTraceElement.getLineNumber());
            logInfo.setMethod(method + "(" + stackTraceElement.getFileName() + ":" + line + ")");
        }
        ringBuffer.publish(ringSeq);
    }

}