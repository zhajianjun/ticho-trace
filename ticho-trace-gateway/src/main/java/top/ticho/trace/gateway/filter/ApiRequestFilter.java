package top.ticho.trace.gateway.filter;

import cn.hutool.core.date.SystemClock;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.useragent.UserAgentUtil;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
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
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@ConditionalOnProperty(value = "ticho.trace.enable", havingValue = "true", matchIfMissing = true)
public class ApiRequestFilter implements GlobalFilter {

    public static final String CACHE_LOG_INFO = "cacheGatewayContext";
    public static final String USER_AGENT = "User-Agent";
    private static final List<String> localhosts = new ArrayList<>();
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
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        String traceId = headers.getFirst(LogConst.TRACE_ID_KEY);
        String spanId = headers.getFirst(LogConst.SPAN_ID_KEY);
        String preAppName = headers.getFirst(LogConst.PRE_APP_NAME_KEY);
        String preIp = headers.getFirst(LogConst.PRE_IP_KEY);
        if (preIp == null) {
            preIp = preIp(request);
        }
        String ip = localIp();
        String appName = environment.getProperty("spring.application.name");
        String trace = traceLogProperty.getTrace();
        TraceUtil.prepare(traceId, spanId, appName, ip, preAppName, preIp, trace);
        traceId = MDC.get(LogConst.TRACE_ID_KEY);
        if (StrUtil.isBlank(traceId)) {
            log.debug("MDC中不存在链路信息,本次调用不传递traceId");
        }
        long millis = SystemClock.now();
        String type = request.getMethodValue();
        String url = request.getPath().pathWithinApplication().value();
        String port = environment.getProperty("server.port");
        String params = JsonUtil.toJsonString(request.getQueryParams());
        LogInfo logInfo = new LogInfo();
        logInfo.setType(type);
        logInfo.setUrl(url);
        logInfo.setPort(port);
        logInfo.setReqParams(params);
        logInfo.setReqHeaders(JsonUtil.toJsonString(headers));
        logInfo.setStart(millis);
        logInfo.setUserAgent(UserAgentUtil.parse(headers.getFirst(USER_AGENT)));
        ServerWebExchange.Builder filter = getBuilder(exchange, logInfo, headers);
        boolean print = Boolean.TRUE.equals(traceLogProperty.getPrint());
        String requestPrefixText = traceLogProperty.getRequestPrefixText();
        if (print) {
            log.info("{} {} {} 请求开始, 请求参数={}, 请求体={}, 请求头={}", requestPrefixText, type, url, params, logInfo.getReqBody(), headers);
        }
        ServerHttpResponseDecorator response = getResponse(exchange, logInfo);
        Integer status = logInfo.getStatus();
        Long consume = logInfo.getConsume();
        String resBody = logInfo.getResBody();
        if (print) {
            log.info("{} {} {} 请求结束, 状态={}, 耗时={}ms, 响应参数={}", requestPrefixText, type, url, status, consume, resBody);
        }
        ServerWebExchange webExchange = filter.response(response).build();
        return chain.filter(webExchange).doFinally(signalType -> {
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
            if (TraceUtil.isOpen()) {
                executor.execute(()-> TracePushContext.push(traceLogProperty.getUrl(), traceCollectInfo));
            }
            TraceUtil.complete();
        });

    }

    // @formatter:on

    private ServerWebExchange.Builder getBuilder(ServerWebExchange exchange, LogInfo logInfo, HttpHeaders headers) {
        MediaType contentType = headers.getContentType();
        long contentLength = headers.getContentLength();
        if (contentLength <= 0) {
            return exchange.mutate();
        }
        if (MediaType.APPLICATION_JSON.equals(contentType) || MediaType.APPLICATION_JSON_UTF8.equals(contentType)) {
            return readBody(exchange, logInfo);
        }
        if (MediaType.APPLICATION_FORM_URLENCODED.equals(contentType)) {
            return readFormData(exchange, logInfo);
        }
        return exchange.mutate();
    }

    private ServerWebExchange.Builder readBody(ServerWebExchange exchange, LogInfo logInfo) {
        ServerHttpRequest request = exchange.getRequest();
        //获取请求体
        String body = getBody(request);
        logInfo.setReqBody(body);
        //下面的将请求体再次封装写回到request里，传到下一级，否则，由于请求体已被消费，后续的服务将取不到值
        ServerHttpRequest newReq = repackageBodyToReq(request, body);
        ServerHttpResponse newRes = getResponse(exchange, logInfo);
        exchange = exchange.mutate().request(newReq).response(newRes).build();
        return exchange.mutate().request(newReq);
    }

    private ServerHttpRequest repackageBodyToReq(ServerHttpRequest request, String body) {
        URI uri = request.getURI();
        request = request.mutate().uri(uri).build();
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);
        Flux<DataBuffer> bodyFlux = Flux.just(buffer);
        return new ServerHttpRequestDecorator(request) {
            @Override
            public Flux<DataBuffer> getBody() {
                return bodyFlux;
            }
        };
    }

    private String getBody(ServerHttpRequest request) {
        Flux<DataBuffer> body = request.getBody();
        AtomicReference<String> bodyRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            DataBufferUtils.release(buffer);
            bodyRef.set(charBuffer.toString());
        });
        return bodyRef.get();
    }

    private ServerWebExchange.Builder readFormData(ServerWebExchange exchange, LogInfo logInfo) {
        MultiValueMap<String, String> formDataMap = new LinkedMultiValueMap<>();
        Mono<MultiValueMap<String, String>> data = exchange.getFormData();
        data.doOnNext(formDataMap::putAll);
        logInfo.setReqParams(JsonUtil.toJsonString(data));
        String formData = getFormData(formDataMap);
        byte[] bodyBytes = formData.getBytes(StandardCharsets.UTF_8);
        int contentLength = bodyBytes.length;
        ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                if (contentLength > 0) {
                    httpHeaders.setContentLength(contentLength);
                } else {
                    httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                }
                return httpHeaders;
            }

            @Override
            public Flux<DataBuffer> getBody() {
                return DataBufferUtils.read(new ByteArrayResource(bodyBytes), new NettyDataBufferFactory(ByteBufAllocator.DEFAULT), contentLength);
            }
        };
        return exchange.mutate().request(decorator);
    }

    private String getFormData(MultiValueMap<String, String> formData) {
        StringBuilder formDataSb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : formData.entrySet()) {
            append(formDataSb, entry.getKey(), entry.getValue());
        }
        String fromData = "";
        if (formDataSb.length() > 0) {
            fromData = formDataSb.substring(0, formDataSb.length() - 1);
        }
        return fromData;
    }

    private void append(StringBuilder formDataSb, String key, List<String> values) {
        values.forEach(value -> formDataSb.append(key).append("=").append(URLUtil.encodeAll(value)).append("&"));
    }


    private ServerHttpResponseDecorator getResponse(ServerWebExchange exchange, LogInfo logInfo) {
        ServerHttpResponse response = exchange.getResponse();
        DataBufferFactory bufferFactory = response.bufferFactory();
        return new ServerHttpResponseDecorator(response) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> publisher) {
                HttpStatus statusCode = getStatusCode();
                if (Objects.equals(statusCode, HttpStatus.OK) && publisher instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = Flux.from(publisher);
                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                        DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                        DataBuffer join = dataBufferFactory.join(dataBuffers);
                        byte[] content = new byte[join.readableByteCount()];
                        join.read(content);
                        DataBufferUtils.release(join);
                        String responseData = new String(content, StandardCharsets.UTF_8);
                        logInfo.setResBody(responseData);
                        logInfo.setStatus(statusCode.value());
                        logInfo.setEnd(SystemClock.now());
                        response.getHeaders().setContentLength(content.length);
                        return bufferFactory.wrap(content);
                    }));
                }
                log.error("获取响应体数据 ：" + statusCode);
                return super.writeWith(publisher);
            }

            @Override
            public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> publisher) {
                return writeWith(Flux.from(publisher).flatMapSequential(p -> p));
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
