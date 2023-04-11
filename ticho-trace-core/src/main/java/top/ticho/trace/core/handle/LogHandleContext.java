package top.ticho.trace.core.handle;

import cn.hutool.core.thread.ThreadUtil;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.TimeoutBlockingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import top.ticho.trace.common.bean.LogInfo;
import top.ticho.trace.common.constant.LogConst;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * 日志处理上下文
 *
 * @author zhajianjun
 * @date 2023-04-10 23:03:49
 */
public class LogHandleContext {
    /** 应用名称 */
    private final String appName;
    private final Disruptor<LogInfo> disruptor;
    /** 日志序号，用于同一时刻日志的排序 */
    private final AtomicLong sequence;
    /** 上一次日志信息的时间戳 */
    private final AtomicLong lastLogTimeStamp;
    /** 日志事件监听 日志事件的处理 */
    private final LogEventListen logEventListen;
    /** 环形存储区 */
    private RingBuffer<LogInfo> ringBuffer;

    public LogHandleContext(String appName, String url, int pushSize, long flushInterval) {
        this.appName = appName;
        // 环形缓冲区大小，必须是2的幂次方
        int bufferSize = 1024;
        // 等待策略，超时
        WaitStrategy waitStrategy = new TimeoutBlockingWaitStrategy(30, TimeUnit.SECONDS);
        // 线程工厂
        ThreadFactory threadFactory = ThreadUtil.newNamedThreadFactory(LogConst.THREAD_NAME_PREFIX_TRACE, false);
        // 事件工厂
        EventFactory<LogInfo> eventFactory = LogInfo::new;
        // 生产者类型，多个
        ProducerType producerType = ProducerType.MULTI;
        this.disruptor = new Disruptor<>(eventFactory, bufferSize, threadFactory, producerType, waitStrategy);
        // 初始化日志事件监听处理器
        this.logEventListen = new LogEventListen(url, pushSize, flushInterval);
        // 注册事件消费者
        disruptor.handleEventsWith(logEventListen);
        this.sequence = new AtomicLong();
        this.lastLogTimeStamp = new AtomicLong();
    }

    /**
     * 日志事件订阅
     */
    public void publish(Consumer<LogInfo> consumer) {
        // 获取下一个序号
        long ringSeq = ringBuffer.next();
        try {
            // 根据序号创建数据
            LogInfo logInfo = ringBuffer.get(ringSeq);
            consumer.accept(logInfo);
            Map<String, String> mdcMap = logInfo.getMdc();
            String appName = Optional.ofNullable(mdcMap.get(LogConst.APP_NAME_KEY)).orElse(this.appName);
            Long dtTime = logInfo.getDtTime();
            // 上一次日志信息的时间戳为空，则默认为当前日志时间戳
            long lastLogTimeStampGet = lastLogTimeStamp.get();
            if (lastLogTimeStampGet == 0) {
                lastLogTimeStamp.set(dtTime);
            }
            // 默认日志序列为0
            long currentSequence = 0;
            // 上一次日志信息的时间戳和日志时间戳相等，序列增加；否则，重置序列为0，并更新上一次日志信息的时间戳
            if (lastLogTimeStampGet == dtTime) {
                currentSequence = sequence.incrementAndGet();
            } else {
                sequence.set(0);
                // 上一次日志信息的时间戳更新
                lastLogTimeStamp.set(dtTime);
            }
            logInfo.setTraceId(mdcMap.get(LogConst.TRACE_ID_KEY));
            logInfo.setSpanId(mdcMap.get(LogConst.SPAN_ID_KEY));
            logInfo.setAppName(appName);
            logInfo.setIp(mdcMap.get(LogConst.IP_KEY));
            logInfo.setPreAppName(mdcMap.get(LogConst.PRE_APP_NAME_KEY));
            logInfo.setPreIp(mdcMap.get(LogConst.PRE_IP_KEY));
            logInfo.setSeq(currentSequence);
        } finally {
            // 事件发布
            ringBuffer.publish(ringSeq);
        }
    }

    public void start() {
        // 启动Disruptor
        ringBuffer = disruptor.start();
        // 日志事件消费者启动
        logEventListen.start();
    }

    public void stop() {
        disruptor.shutdown();
        logEventListen.shutdown();
    }


}
