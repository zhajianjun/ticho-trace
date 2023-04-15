package com.ticho.trace.okhttp.interceptor;

import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.MDC;
import com.ticho.trace.common.constant.LogConst;
import com.ticho.trace.core.util.TraceUtil;

import java.io.IOException;

/**
 * okhttp3链路追踪传递
 *
 * @author zhajianjun
 * @date 2022-11-01 14:46
 */
@Slf4j
public class OkHttpLogInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        long t1 = SystemClock.now();
        Request req = chain.request();
        String traceId = MDC.get(LogConst.TRACE_ID_KEY);
        if (StrUtil.isBlank(traceId)) {
            log.debug("MDC中不存在链路信息,本次调用不传递traceId");
            return chain.proceed(req);
        }
        Request.Builder builder = req.newBuilder();
        builder.addHeader(LogConst.TRACE_ID_KEY, traceId);
        builder.addHeader(LogConst.SPAN_ID_KEY, TraceUtil.nextSpanId());
        builder.addHeader(LogConst.PRE_APP_NAME_KEY, MDC.get(LogConst.APP_NAME_KEY));
        builder.addHeader(LogConst.PRE_IP_KEY, MDC.get(LogConst.IP_KEY));
        req = builder.build();
        return chain.proceed(req);
    }

}
