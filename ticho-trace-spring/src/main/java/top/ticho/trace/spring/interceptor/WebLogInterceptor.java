package top.ticho.trace.spring.interceptor;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import top.ticho.trace.common.bean.TraceCollectInfo;
import top.ticho.trace.common.constant.LogConst;
import top.ticho.trace.core.json.JsonUtil;
import top.ticho.trace.core.util.TraceUtil;
import top.ticho.trace.spring.component.SpringTracePushContext;
import top.ticho.trace.spring.prop.SpringTraceLogProperty;
import top.ticho.trace.spring.util.IpUtil;
import top.ticho.trace.spring.wrapper.RequestWrapper;
import top.ticho.trace.spring.wrapper.ResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class WebLogInterceptor implements HandlerInterceptor, InitializingBean {

    private static final String NONE = "NONE";

    private static TransmittableThreadLocal<LogInfo> logThreadLocal;

    private final TransmittableThreadLocal<LogInfo> theadLocal;

    private final SpringTraceLogProperty springTraceLogProperty;

    private final SpringTracePushContext springTracePushContext;

    private final Environment environment;

    public WebLogInterceptor(SpringTraceLogProperty springTraceLogProperty, SpringTracePushContext springTracePushContext,
            Environment environment) {
        this.theadLocal = new TransmittableThreadLocal<>();
        this.springTraceLogProperty = springTraceLogProperty;
        this.springTracePushContext = springTracePushContext;
        this.environment = environment;
    }

    @Override
    public void afterPropertiesSet() {
        logThreadLocal = this.theadLocal;
    }

    public LogInfo getLogInfo() {
        return theadLocal.get();
    }

    public static LogInfo logInfo() {
        return logThreadLocal.get();
    }

    // @formatter:off

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        // 是否打印日志
        if (!(request instanceof RequestWrapper) || !(handler instanceof HandlerMethod)) {
            return true;
        }
        long millis = SystemClock.now();
        String method = request.getMethod();
        String url = request.getRequestURI();
        // params
        Map<String, Object> paramsMap = getParams(request);
        String params = toJsonOfDefault(paramsMap);
        // body
        RequestWrapper requestWrapper = (RequestWrapper) request;
        String body = nullOfDefault(requestWrapper.getBody());
        // header
        Map<String, String> headersMap = getHeaders(request);
        String headers = toJsonOfDefault(headersMap);
        String requestPrefixText = springTraceLogProperty.getRequestPrefixText();
        String trace = springTraceLogProperty.getTrace();
        UserAgent userAgent = IpUtil.getUserAgent(request);
        Principal principal = request.getUserPrincipal();
        String traceId = headersMap.get(LogConst.TRACE_ID_KEY);
        String spanId = headersMap.get(LogConst.SPAN_ID_KEY);
        String appName = environment.getProperty("spring.application.name");
        String port = environment.getProperty("server.port");
        String ip = IpUtil.getIp(request);
        String preAppName = headersMap.get(LogConst.PRE_IP_KEY);
        String preIp = headersMap.get(LogConst.PRE_APP_NAME_KEY);
        TraceUtil.prepare(traceId, spanId, appName, ip, preAppName, preIp, trace);
        LogInfo logInfo = LogInfo.builder()
            .type(method)
            .url(url)
            .port(port)
            .reqParams(params)
            .reqBody(body)
            .reqHeaders(headers)
            .start(millis)
            .startTime(LocalDateTimeUtil.of(millis, ZoneId.of("UTC+8")))
            .username((principal != null ? principal.getName() : null))
            .userAgent(userAgent)
            .handlerMethod((HandlerMethod) handler)
            .build();
        theadLocal.set(logInfo);
        boolean print = Boolean.TRUE.equals(springTraceLogProperty.getPrint());
        if (print) {
            log.info("{} {} {} 请求开始, 请求参数={}, 请求体={}, 请求头={}", requestPrefixText, method, url, params, body, headers);
        }
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        LogInfo logInfo = theadLocal.get();
        if (logInfo == null) {
            return;
        }
        String method = request.getMethod();
        String url = request.getRequestURI();
        String resBody = nullOfDefault(getResBody(response));
        logInfo.setResBody(resBody);
        String requestPrefixText = springTraceLogProperty.getRequestPrefixText();
        theadLocal.remove();
        long now = SystemClock.now();
        logInfo.setEnd(now);
        logInfo.setEndTime(LocalDateTimeUtil.of(now));
        int status = response.getStatus();
        Long time = logInfo.getConsume();
        boolean print = Boolean.TRUE.equals(springTraceLogProperty.getPrint());
        if (print) {
            log.info("{} {} {} 请求结束, 状态={}, 耗时={}ms, 响应参数={}", requestPrefixText, method, url, status, time, resBody);
        }
        String traceUrl = springTraceLogProperty.getUrl();
        HandlerMethod handlerMethod = logInfo.getHandlerMethod();
        TraceCollectInfo traceCollectInfo = TraceCollectInfo.builder()
            .traceId(MDC.get(LogConst.TRACE_ID_KEY))
            .spanId(MDC.get(LogConst.SPAN_ID_KEY))
            .appName(MDC.get(LogConst.APP_NAME_KEY))
            .ip(MDC.get(LogConst.IP_KEY))
            .preAppName(MDC.get(LogConst.PRE_APP_NAME_KEY))
            .preIp(MDC.get(LogConst.PRE_IP_KEY))
            .url(logInfo.getUrl())
            .port(logInfo.getPort())
            .method(handlerMethod.toString())
            .type(logInfo.getType())
            .status(status)
            .start(logInfo.getStart())
            .end(logInfo.getEnd())
            .consume(logInfo.getConsume())
            .build();
        springTracePushContext.push(traceUrl, traceCollectInfo);
        TraceUtil.complete();
    }

    private String getResBody(HttpServletResponse response) {
        String contentType = response.getContentType();
        boolean flag = contentType != null && (contentType.startsWith(MediaType.APPLICATION_JSON_VALUE) ||
            contentType.equals(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
        if (!flag) {
            return NONE;
        }
        ResponseWrapper responseWrapper = (ResponseWrapper)response;
        return responseWrapper.getBody();
    }

    public Map<String, Object> getParams(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        Enumeration<String> parameteNames = request.getParameterNames();
        while (parameteNames.hasMoreElements()) {
            //获得每个文本域的name
            String name = parameteNames.nextElement();
            //根据文本域的name来获取值
            //因为无法判断文本域是否是单值或者双值，所以我们全部使用双值接收
            String[] values = request.getParameterValues(name);
            String value = String.join(",", Arrays.stream(values).filter(StrUtil::isNotBlank).collect(Collectors.joining(",")));
            map.put(name, value);
        }
        return map;
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

    public Map<String, String> getHeaders(HttpServletResponse response) {
        Map<String, String> map = new HashMap<>();
        Collection<String> headerNames = response.getHeaderNames();
        for (String name : headerNames) {
            String value = response.getHeader(name);
            map.put(name, value);
        }
        return map;
    }

    private String toJsonOfDefault(Map<String, ?> map) {
        String result = JsonUtil.toJsonString(map);
        return nullOfDefault(result);
    }

    private String nullOfDefault(String result) {
        if (result == null || result.isEmpty()) {
            return NONE;
        }
        return result;
    }
}
