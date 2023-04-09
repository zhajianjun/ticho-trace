package top.ticho.trace.gateway.filter;

import cn.hutool.core.date.SystemClock;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.ticho.trace.common.bean.LogInfo;
import top.ticho.trace.common.bean.TraceCollectInfo;
import top.ticho.trace.common.constant.LogConst;
import top.ticho.trace.common.prop.TraceLogProperty;
import top.ticho.trace.core.json.JsonUtil;
import top.ticho.trace.core.push.TracePushContext;
import top.ticho.trace.core.util.TraceUtil;

import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-04-03 11:05
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "ticho.trace.enable", havingValue = "true", matchIfMissing = true)
public class TraceGlobalFilter implements GlobalFilter, Ordered {
    public static final String USER_AGENT = "User-Agent";

    private static final List<String> localhosts = new ArrayList<>();
    private final TransmittableThreadLocal<LogInfo> theadLocal = new TransmittableThreadLocal<>();
    private final ThreadPoolExecutor executor = ThreadUtil.newExecutorByBlockingCoefficient(0.8f);

    static{
        localhosts.add("127.0.0.1");
        localhosts.add("0:0:0:0:0:0:0:1");
    }

    @Autowired
    private Environment environment;

    @Autowired
    private TraceLogProperty traceLogProperty;

    @PreDestroy
    public void destroy() {
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


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        LogInfo logInfo = new LogInfo();
        return chain.filter(preHandle(exchange, logInfo)).doFinally(signalType -> {
            boolean print = Boolean.TRUE.equals(traceLogProperty.getPrint());
            String requestPrefixText = traceLogProperty.getRequestPrefixText();
            String type = logInfo.getType();
            String url = logInfo.getUrl();
            Long consume = logInfo.getConsume();
            Integer status = logInfo.getStatus();
            String resBody = logInfo.getResBody();
            if (print) {
                log.info("{} {} {} 请求结束, 状态={}, 耗时={}ms, 响应参数={}", requestPrefixText, type, url, status, consume, resBody);
            }
            TraceCollectInfo traceCollectInfo = TraceCollectInfo.builder()
                .traceId(MDC.get(LogConst.TRACE_ID_KEY))
                .spanId(MDC.get(LogConst.SPAN_ID_KEY))
                .appName(MDC.get(LogConst.APP_NAME_KEY))
                .ip(MDC.get(LogConst.IP_KEY))
                .preAppName(MDC.get(LogConst.PRE_APP_NAME_KEY))
                .preIp(MDC.get(LogConst.PRE_IP_KEY))
                .url(logInfo.getUrl())
                .port(logInfo.getPort())
                .method("")
                .type(logInfo.getType())
                .status(logInfo.getStatus())
                .start(logInfo.getStart())
                .end(logInfo.getEnd())
                .consume(logInfo.getConsume())
                .build();
            executor.execute(()-> TracePushContext.push(traceLogProperty.getUrl(), traceCollectInfo));
            theadLocal.remove();
            TraceUtil.complete();
        });
    }

    public ServerWebExchange preHandle(ServerWebExchange exchange, LogInfo logInfo) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        HttpHeaders headers = serverHttpRequest.getHeaders();
        String traceId = headers.getFirst(LogConst.TRACE_ID_KEY);
        String spanId = headers.getFirst(LogConst.SPAN_ID_KEY);
        String preAppName = headers.getFirst(LogConst.PRE_APP_NAME_KEY);
        String preIp = headers.getFirst(LogConst.PRE_IP_KEY);
        if (preIp == null) {
            preIp = preIp(serverHttpRequest);
        }
        MultiValueMap<String, String> queryParams = serverHttpRequest.getQueryParams();
        String params = JsonUtil.toJsonString(queryParams);
        String ip = localIp();
        String appName = environment.getProperty("spring.application.name");
        String trace = traceLogProperty.getTrace();
        String type = serverHttpRequest.getMethodValue();
        String url = serverHttpRequest.getPath().toString();
        logInfo.setUrl(url);
        logInfo.setPort(environment.getProperty("server.port"));
        logInfo.setStart(SystemClock.now());
        logInfo.setType(type);
        logInfo.setReqParams(params);
        TraceUtil.prepare(traceId, spanId, appName, ip, preAppName, preIp, trace);
        traceId = MDC.get(LogConst.TRACE_ID_KEY);
        String finalTraceId = traceId;
        Consumer<HttpHeaders> httpHeaders = httpHeader -> {
            httpHeader.set(LogConst.TRACE_ID_KEY, finalTraceId);
            httpHeader.set(LogConst.SPAN_ID_KEY, TraceUtil.nextSpanId());
            httpHeader.set(LogConst.PRE_APP_NAME_KEY, appName);
            httpHeader.set(LogConst.PRE_IP_KEY, ip);
        };
        boolean print = Boolean.TRUE.equals(traceLogProperty.getPrint());
        String requestPrefixText = traceLogProperty.getRequestPrefixText();
        if (print) {
            log.info("{} {} {} 请求开始, 请求参数={}, 请求体={}, 请求头={}", requestPrefixText, type, url, params, logInfo.getReqBody(), headers);
        }
        ServerHttpRequest newRequest = serverHttpRequest.mutate().headers(httpHeaders).build();
        ServerHttpResponse response = getResponse(exchange, logInfo);
        return exchange.mutate().request(newRequest).response(response).build();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    public ServerHttpResponse getResponse(ServerWebExchange exchange, LogInfo logInfo){
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        return new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body1) {
                HttpStatus statusCode = getStatusCode();
                if (Objects.equals(statusCode, HttpStatus.OK) && body1 instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = Flux.from(body1);
                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                        DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                        DataBuffer join = dataBufferFactory.join(dataBuffers);
                        byte[] content = new byte[join.readableByteCount()];
                        join.read(content);
                        DataBufferUtils.release(join);
                        String responseData = new String(content, StandardCharsets.UTF_8);
                        logInfo.setEnd(SystemClock.now());
                        logInfo.setResBody(responseData);
                        logInfo.setStatus(statusCode.value());
                        originalResponse.getHeaders().setContentLength(content.length);
                        return bufferFactory.wrap(content);
                    }));
                } else {
                    log.error("获取响应体数据 ：" + statusCode);
                }
                return super.writeWith(body1);
            }

            @Override
            public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body1) {
                return writeWith(Flux.from(body1).flatMapSequential(p -> p));
            }
        };
    }

    public static String preIp(ServerHttpRequest request) {
        HttpHeaders httpHeaders = request.getHeaders();
        String ip = httpHeaders.getFirst("x-forwarded-for");
        // Proxy-Client-IP 这个一般是经过apache http服务器的请求才会有，用apache http做代理时一般会加上Proxy-Client-IP请求头，而WL-Proxy-Client-IP是他的weblogic插件加上的头。
        String unknown = "unknown";
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = httpHeaders.getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = httpHeaders.getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = Optional.ofNullable(request.getRemoteAddress())
                .map(InetSocketAddress::getAddress)
                .map(InetAddress::getHostAddress)
                .orElse(null);
        }
        if (StrUtil.isBlank(ip)) {
            return "";
        }
        int index = ip.indexOf(",");
        if (index != -1) {
            return ip.substring(0, index);
        }
        if (!localhosts.contains(ip)) {
            return ip;
        }
        // 获取本机真正的ip地址
        return localIp();
    }

    public static String localIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }

}
