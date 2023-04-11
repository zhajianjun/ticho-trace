package top.ticho.trace.gateway.filter;

import cn.hutool.core.date.SystemClock;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.useragent.UserAgentUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
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
import top.ticho.trace.common.bean.HttpLogInfo;
import top.ticho.trace.common.bean.TraceInfo;
import top.ticho.trace.common.constant.LogConst;
import top.ticho.trace.common.prop.TraceLogProperty;
import top.ticho.trace.core.json.JsonUtil;
import top.ticho.trace.core.push.TracePushContext;
import top.ticho.trace.core.util.TraceUtil;

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
import java.util.concurrent.atomic.AtomicReference;
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
public class ApiFilter implements GlobalFilter, Ordered {
    public static final String USER_AGENT = "User-Agent";

    private static final List<String> localhosts = new ArrayList<>();
    private final TransmittableThreadLocal<HttpLogInfo> theadLocal = new TransmittableThreadLocal<>();
    private final ThreadPoolExecutor executor = ThreadUtil.newExecutorByBlockingCoefficient(0.8f);

    static{
        localhosts.add("127.0.0.1");
        localhosts.add("0:0:0:0:0:0:0:1");
    }

    @Autowired
    private Environment environment;

    @Autowired
    private TraceLogProperty traceLogProperty;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(preHandle(exchange)).doFinally(signalType -> {
            // @formatter:off
            HttpLogInfo httpLogInfo = theadLocal.get();
            if (httpLogInfo == null) {
                return;
            }
            long end = SystemClock.now();
            httpLogInfo.setEnd(end);
            boolean print = Boolean.TRUE.equals(traceLogProperty.getPrint());
            String requestPrefixText = traceLogProperty.getReqPrefix();
            String type = httpLogInfo.getType();
            String url = httpLogInfo.getUrl();
            Long consume = httpLogInfo.getConsume();
            Integer status = httpLogInfo.getStatus();
            String resBody = httpLogInfo.getResBody();
            if (print) {
                log.info("{} {} {} 请求结束, 状态={}, 耗时={}ms, 响应参数={}", requestPrefixText, type, url, status, consume, resBody);
            }
            TraceInfo traceInfo = TraceInfo.builder()
                    .traceId(MDC.get(LogConst.TRACE_ID_KEY))
                    .spanId(MDC.get(LogConst.SPAN_ID_KEY))
                    .appName(MDC.get(LogConst.APP_NAME_KEY))
                    .ip(MDC.get(LogConst.IP_KEY))
                    .preAppName(MDC.get(LogConst.PRE_APP_NAME_KEY))
                    .preIp(MDC.get(LogConst.PRE_IP_KEY))
                    .url(httpLogInfo.getUrl())
                    .port(httpLogInfo.getPort())
                    .method("")
                    .type(httpLogInfo.getType())
                    .status(httpLogInfo.getStatus())
                    .start(httpLogInfo.getStart())
                    .end(end)
                    .consume(httpLogInfo.getConsume())
                    .build();
            TracePushContext.pushTraceInfoAsync(traceLogProperty.getUrl(), traceInfo);
            theadLocal.remove();
            TraceUtil.complete();
            // @formatter:on
        });
    }

    public ServerWebExchange preHandle(ServerWebExchange exchange) {
        HttpLogInfo httpLogInfo = new HttpLogInfo();
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        HttpHeaders headers = serverHttpRequest.getHeaders();
        MultiValueMap<String, String> queryParams = serverHttpRequest.getQueryParams();
        String params = JsonUtil.toJsonString(queryParams);
        serverHttpRequest = getServerHttpRequest(exchange, httpLogInfo, headers);

        String headersStr = JsonUtil.toJsonString(headers);
        String traceId = headers.getFirst(LogConst.TRACE_ID_KEY);
        String spanId = headers.getFirst(LogConst.SPAN_ID_KEY);
        String preAppName = headers.getFirst(LogConst.PRE_APP_NAME_KEY);
        String preIp = headers.getFirst(LogConst.PRE_IP_KEY);
        if (preIp == null) {
            preIp = preIp(serverHttpRequest);
        }
        String ip = localIp();
        String appName = environment.getProperty("spring.application.name");
        String port = environment.getProperty("server.port");
        String trace = traceLogProperty.getTrace();
        TraceUtil.prepare(traceId, spanId, appName, ip, preAppName, preIp, trace);
        traceId = MDC.get(LogConst.TRACE_ID_KEY);
        if (StrUtil.isBlank(traceId)) {
            log.debug("MDC中不存在链路信息,本次调用不传递traceId");
            return exchange;
        }
        long millis = SystemClock.now();
        String finalTraceId = traceId;
        String type = serverHttpRequest.getMethodValue();
        String url = serverHttpRequest.getPath().toString();
        httpLogInfo.setType(type);
        httpLogInfo.setUrl(url);
        httpLogInfo.setPort(port);
        httpLogInfo.setReqParams(params);
        httpLogInfo.setReqHeaders(headersStr);
        httpLogInfo.setStart(millis);
        httpLogInfo.setUserAgent(UserAgentUtil.parse(headers.getFirst(USER_AGENT)));
        boolean print = Boolean.TRUE.equals(traceLogProperty.getPrint());
        String requestPrefixText = traceLogProperty.getReqPrefix();
        if (print) {
            log.info("{} {} {} 请求开始, 请求参数={}, 请求体={}, 请求头={}", requestPrefixText, type, url, params, httpLogInfo.getReqBody(), headers);
        }
        theadLocal.set(httpLogInfo);
        Consumer<HttpHeaders> httpHeaders = httpHeader -> {
            httpHeader.set(LogConst.TRACE_ID_KEY, finalTraceId);
            httpHeader.set(LogConst.SPAN_ID_KEY, TraceUtil.nextSpanId());
            httpHeader.set(LogConst.PRE_APP_NAME_KEY, appName);
            httpHeader.set(LogConst.PRE_IP_KEY, ip);
        };

        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        ServerHttpResponseDecorator response = new ServerHttpResponseDecorator(originalResponse) {
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
                        //
                        httpLogInfo.setResBody(responseData);
                        httpLogInfo.setStatus(statusCode.value());
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


        ServerHttpRequest newRequest = serverHttpRequest.mutate().headers(httpHeaders).build();
        return exchange.mutate().request(newRequest).response(response).build();
    }

    private ServerHttpRequest getServerHttpRequest(ServerWebExchange exchange, HttpLogInfo httpLogInfo, HttpHeaders headers) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        long contentLength = headers.getContentLength();
        if (contentLength <= 0) {
            return serverHttpRequest;
        }
        MediaType contentType = headers.getContentType();
        if (MediaType.APPLICATION_JSON.equals(contentType) || MediaType.APPLICATION_JSON_UTF8.equals(contentType)) {
            //从请求里获取Post请求体
            String body = resolveBodyFromRequest(serverHttpRequest);
            httpLogInfo.setReqBody(body);
            //下面的将请求体再次封装写回到request里，传到下一级，否则，由于请求体已被消费，后续的服务将取不到值
            URI uri = serverHttpRequest.getURI();
            serverHttpRequest = serverHttpRequest.mutate().uri(uri).build();
            DataBuffer bodyDataBuffer = stringBuffer(body);
            Flux<DataBuffer> bodyFlux = Flux.just(bodyDataBuffer);
            return new ServerHttpRequestDecorator(serverHttpRequest) {
                @Override
                public Flux<DataBuffer> getBody() {
                    return bodyFlux;
                }
            };
        }
        if (MediaType.APPLICATION_FORM_URLENCODED.equals(contentType)) {
            return readFormData(exchange, httpLogInfo);
        }
        return serverHttpRequest;
    }

    private ServerHttpRequest readFormData(ServerWebExchange exchange, HttpLogInfo httpLogInfo) {
        MultiValueMap<String, String> formDataMap = new LinkedMultiValueMap<>();
        Mono<MultiValueMap<String, String>> data = exchange.getFormData();
        data.doOnNext(formDataMap::putAll);
        httpLogInfo.setReqParams(JsonUtil.toJsonString(data));
        String formData = getFormData(formDataMap);
        byte[] bodyBytes = formData.getBytes(StandardCharsets.UTF_8);
        int contentLength = bodyBytes.length;
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
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

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * 从Flux<DataBuffer>中获取字符串的方法
     * @return 请求体
     */
    private String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {
        //获取请求体
        Flux<DataBuffer> body = serverHttpRequest.getBody();
        AtomicReference<String> bodyRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            DataBufferUtils.release(buffer);
            bodyRef.set(charBuffer.toString());
        });
        //获取request body
        return bodyRef.get();
    }

    private DataBuffer stringBuffer(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);
        return buffer;
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
