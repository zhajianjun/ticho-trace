package top.ticho.trace.spring.interceptor;

import cn.hutool.core.date.SystemClock;
import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import top.ticho.trace.common.bean.TraceInfo;
import top.ticho.trace.common.constant.LogConst;
import top.ticho.trace.common.prop.TraceProperty;
import top.ticho.trace.core.push.TracePushContext;
import top.ticho.trace.core.util.TraceUtil;
import top.ticho.trace.spring.util.IpUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 链路拦截器
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
@Slf4j
public class TraceInterceptor implements HandlerInterceptor, Ordered {

    /** 接口开始时间 */
    private final TransmittableThreadLocal<Long> startLocal;
    /** 链路配置 */
    private final TraceProperty traceProperty;
    /** 环境变量 */
    private final Environment environment;

    public TraceInterceptor(TraceProperty traceProperty, Environment environment) {
        this.startLocal = new TransmittableThreadLocal<>();
        this.traceProperty = traceProperty;
        this.environment = environment;
    }

    // @formatter:off

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        startLocal.set(SystemClock.now());
        Map<String, String> headersMap = getHeaders(request);
        String trace = traceProperty.getTrace();
        String appName = environment.getProperty("spring.application.name");
        String traceId = headersMap.get(LogConst.TRACE_ID_KEY);
        String spanId = headersMap.get(LogConst.SPAN_ID_KEY);
        String preAppName = headersMap.get(LogConst.PRE_APP_NAME_KEY);
        String preIp = headersMap.get(LogConst.PRE_IP_KEY);
        String ip = IpUtil.localIp();
        if (preIp == null) {
            preIp = IpUtil.preIp(request);
        }
        TraceUtil.prepare(traceId, spanId, appName, ip, preAppName, preIp, trace);
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        // @formatter:off
        if (!(handler instanceof HandlerMethod)) {
            return;
        }
        Long start = startLocal.get();
        String type = request.getMethod();
        String url = request.getRequestURI();
        String port = environment.getProperty("server.port");
        long end = SystemClock.now();
        int status = response.getStatus();
        Long consume = end - start;
        String traceUrl = traceProperty.getUrl();
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        TraceInfo traceInfo = TraceInfo.builder()
            .traceId(MDC.get(LogConst.TRACE_ID_KEY))
            .spanId(MDC.get(LogConst.SPAN_ID_KEY))
            .appName(MDC.get(LogConst.APP_NAME_KEY))
            .ip(MDC.get(LogConst.IP_KEY))
            .preAppName(MDC.get(LogConst.PRE_APP_NAME_KEY))
            .preIp(MDC.get(LogConst.PRE_IP_KEY))
            .url(url)
            .port(port)
            .method(handlerMethod.toString())
            .type(type)
            .status(status)
            .start(start)
            .end(end)
            .consume(consume)
            .build();
        if (traceProperty.getPushTrace()) {
            TracePushContext.pushTraceInfoAsync(traceUrl, traceInfo);
        }
        TraceUtil.complete();
        startLocal.remove();
        // @formatter:on
    }


    public Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            //获得每个文本域的name
            String name = headerNames.nextElement();
            //根据文本域的name来获取值
            //因为无法判断文本域是否是单值或者双值，所以我们全部使用双值接收
            String value = request.getHeader(name);
            map.put(name, value);
        }
        return map;
    }

    @Override
    public int getOrder() {
        return traceProperty.getOrder();
    }

}
