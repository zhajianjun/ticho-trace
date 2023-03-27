package top.ticho.trace.core.push;

import top.ticho.trace.core.push.adapter.OkHttpTracePushAdapter;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
public class TracePushContext {
    private TracePushContext() {
    }

    private static TracePushAdapter TRACE_PUSH_ADAPTER;

    static {
        TRACE_PUSH_ADAPTER = new OkHttpTracePushAdapter();
    }

    public static void setTracePushAdapter(TracePushAdapter tracePushAdapter) {
        TRACE_PUSH_ADAPTER = tracePushAdapter;
    }

    public static void push(String url, Object data){
        TRACE_PUSH_ADAPTER.push(url, data);
    }



}
