package top.ticho.trace.core.push.adapter;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import top.ticho.trace.core.json.JsonUtil;
import top.ticho.trace.core.push.TracePushAdapter;

import java.io.IOException;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
public class OkHttpTracePushAdapter implements TracePushAdapter {
    /** http客户端 */
    private final OkHttpClient httpClient = new OkHttpClient.Builder().build();

    @Override
    public void push(String url, Object data) {
        String json = JsonUtil.toJsonString(data);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        try(Response execute = httpClient.newCall(request).execute()) {
        } catch (IOException e) {
            System.out.println("okhttp Failed to push data " + e.getMessage());
        }
    }


}
