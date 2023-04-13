package top.ticho.trace.core.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.Setter;
import top.ticho.trace.common.constant.LogConst;
import top.ticho.trace.core.handle.LogHandleContext;

import java.util.HashMap;
import java.util.Map;

/**
 * 分布式日志appender
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
public class DistributedLogAppender extends AppenderBase<ILoggingEvent> {

    /** 应用名称 */
    @Setter
    private String appName = LogConst.UNKNOWN;
    /** 日志推送的url */
    @Setter
    private String url;
    /** 批量推送的日志数量，达到一定数量则进行推送日志 */
    @Setter
    private int pushSize = 100;
    /** 日志推送的时间间隔，单位：毫秒（ms） */
    @Setter
    private int flushInterval = 500;
    /** 是否推送日志 */
    @Setter
    private Boolean pushLog = false;
    /** 日志处理上下文 */
    private LogHandleContext logHandleContext;


    @Override
    public void start() {
        super.start();
        if (!pushLog) {
            return;
        }
        this.logHandleContext = new LogHandleContext(appName, url, pushSize, flushInterval);
        logHandleContext.start();
    }

    @Override
    public void stop() {
        super.stop();
        if (!pushLog) {
            return;
        }
        logHandleContext.stop();
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (event == null) {
            return;
        }
        logHandleContext.publish(logInfo -> {
            Map<String, String> mdcMap = new HashMap<>(event.getMDCPropertyMap());
            logInfo.setLogLevel(event.getLevel().toString());
            logInfo.setDtTime(event.getTimeStamp());
            logInfo.setClassName(event.getLoggerName());
            logInfo.setContent(event.getFormattedMessage());
            logInfo.setThreadName(event.getThreadName());
            logInfo.setMdc(mdcMap);
            String method = "";
            StackTraceElement[] stackTraceElements = event.getCallerData();
            if (stackTraceElements != null && stackTraceElements.length > 0) {
                StackTraceElement stackTraceElement = stackTraceElements[0];
                method = stackTraceElement.getMethodName();
                String line = String.valueOf(stackTraceElement.getLineNumber());
                method = method + "(" + stackTraceElement.getFileName() + ":" + line + ")";
            }
            logInfo.setMethod(method);
        });
    }

}