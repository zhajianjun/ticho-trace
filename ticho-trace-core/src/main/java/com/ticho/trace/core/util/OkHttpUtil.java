package com.ticho.trace.core.util;

import com.ticho.trace.common.constant.LogConst;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

/**
 * 使用okhttp进行适配
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
public class OkHttpUtil {
    /** http客户端 */
    private static final OkHttpClient httpClient = new OkHttpClient.Builder().build();

    public static void push(String url, String secret, Object data) {
        // @formatter:off
        String json = JsonUtil.toJsonString(data);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        Request request = new Request.Builder()
            .url(url)
            .addHeader(LogConst.SECRET_KEY, secret)
            .post(requestBody)
            .build();
        try (Response execute = httpClient.newCall(request).execute()) {
        } catch (IOException e) {
            System.err.printf("[%s] okhttp Failed to push data to %s error:%s%n", Thread.currentThread().getName(), url, e.getMessage());
        }
        // @formatter:on
    }


}
