package com.ticho.trace.spring.aop;

import cn.hutool.core.date.SystemClock;
import cn.hutool.extra.spring.SpringUtil;
import com.ticho.trace.common.bean.TraceInfo;
import com.ticho.trace.common.constant.LogConst;
import com.ticho.trace.common.prop.TraceProperty;
import com.ticho.trace.core.handle.TracePushContext;
import com.ticho.trace.core.util.TraceUtil;
import com.ticho.trace.spring.event.TraceEvent;
import com.ticho.trace.spring.util.IpUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

/**
 * 通用链路处理
 *
 * @author zhajianjun
 * @date 2023-06-09 11:35
 */
public abstract class AbstractAspect {

    @Resource
    private Environment environment;

    @Resource
    private TraceProperty traceProperty;

    public Object trace(ProceedingJoinPoint joinPoint, String preAppName, String preIp) throws Throwable {
        // @formatter:off
        long start = SystemClock.now();
        try {
            String appName = environment.getProperty("spring.application.name");
            String ip = IpUtil.localIp();
            TraceUtil.prepare(null, null, appName, ip, preAppName, preIp, null);
            return joinPoint.proceed();
        } finally {
            String env = environment.getProperty("spring.profiles.active");
            long end = SystemClock.now();
            Long consume = end - start;
            TraceInfo traceInfo = TraceInfo.builder()
                .traceId(MDC.get(LogConst.TRACE_ID_KEY))
                .spanId(MDC.get(LogConst.SPAN_ID_KEY))
                .appName(MDC.get(LogConst.APP_NAME_KEY))
                .env(env)
                .ip(MDC.get(LogConst.IP_KEY))
                .preAppName(MDC.get(LogConst.PRE_APP_NAME_KEY))
                .preIp(MDC.get(LogConst.PRE_IP_KEY))
                // .url(url)
                // .port(port)
                // .method(handlerMethod.toString())
                // .type(type)
                // .status(status)
                .start(start)
                .end(end)
                .consume(consume)
                .build();
            TracePushContext.asyncPushTrace(traceProperty, traceInfo);
            ApplicationContext applicationContext = SpringUtil.getApplicationContext();
            applicationContext.publishEvent(new TraceEvent(applicationContext, traceInfo));
            TraceUtil.complete();
        }
        // @formatter:on
    }


}
