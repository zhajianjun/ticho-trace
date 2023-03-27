package top.ticho.trace.spring.component;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import top.ticho.trace.core.push.TracePushContext;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-03-27 17:25
 */
@Component
public class SpringTracePushContext {

    @Async
    public void push(String url, Object data) {
        TracePushContext.push(url, data);
    }

}
