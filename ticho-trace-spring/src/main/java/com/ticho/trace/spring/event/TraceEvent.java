package top.ticho.trace.spring.event;

import top.ticho.trace.common.bean.TraceInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

/**
 * 链路追踪事件
 *
 * @author zhajianjun
 * @date 2023-02-14 16:23
 */
@Slf4j
@Getter
public class TraceEvent extends ApplicationContextEvent {

    private final TraceInfo traceInfo;

    public TraceEvent(ApplicationContext source, TraceInfo traceInfo) {
        super(source);
        this.traceInfo = traceInfo;
    }

}
