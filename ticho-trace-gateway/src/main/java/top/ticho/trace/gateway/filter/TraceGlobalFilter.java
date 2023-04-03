package top.ticho.trace.gateway.filter;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import top.ticho.trace.common.constant.LogConst;
import top.ticho.trace.common.prop.TraceLogProperty;
import top.ticho.trace.core.util.TraceUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-04-03 11:05
 */
@Slf4j
@ConditionalOnProperty(value = "ticho.trace.enable", havingValue = "true", matchIfMissing = true)
public class TraceGlobalFilter implements GlobalFilter, Ordered {

    private static final List<String> localhosts = new ArrayList<>();

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
        return chain.filter(preHandle(exchange)).doFinally(signalType -> TraceUtil.complete());
    }

    public ServerWebExchange preHandle(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        String traceId = headers.getFirst(LogConst.TRACE_ID_KEY);
        String spanId = headers.getFirst(LogConst.SPAN_ID_KEY);
        String preAppNam = headers.getFirst(LogConst.PRE_APP_NAME_KEY);
        String preIp = headers.getFirst(LogConst.PRE_IP_KEY);
        if (preIp == null) {
            preIp = preIp(request);
        }
        String ip = localIp();
        String appName = environment.getProperty("spring.application.name");
        String trace = traceLogProperty.getTrace();
        TraceUtil.prepare(traceId, spanId, appName, ip, preAppNam, preIp, trace);
        if (StrUtil.isBlank(traceId)) {
            log.debug("MDC中不存在链路信息,本次调用不传递traceId");
            return exchange;
        }
        Consumer<HttpHeaders> httpHeaders = httpHeader -> {
            httpHeader.set(LogConst.TRACE_ID_KEY, traceId);
            httpHeader.set(LogConst.SPAN_ID_KEY, TraceUtil.nextSpanId());
            httpHeader.set(LogConst.PRE_APP_NAME_KEY, appName);
            httpHeader.set(LogConst.PRE_IP_KEY, ip);
        };
        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().headers(httpHeaders).build();
        return exchange.mutate().request(serverHttpRequest).build();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
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
