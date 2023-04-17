package com.ticho.trace.core.json;

import com.ticho.trace.core.json.adapter.HutoolJsonAdapter;

import java.util.List;
import java.util.Map;

/**
 * json工具类
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
public class JsonUtil {

    private JsonUtil() {
    }

    private static JsonAdapter JSON_ADAPTER;

    static {
        JSON_ADAPTER = new HutoolJsonAdapter();
    }

    public static void setJsonAdapter(com.ticho.trace.core.json.JsonAdapter jsonAdapter) {
        JSON_ADAPTER = jsonAdapter;
    }


    public static String toJsonString(Object obj) {
        return JSON_ADAPTER.toJsonString(obj);
    }

    public static <T> T toJavaObject(String jsonString, Class<T> clazz) {
        return JSON_ADAPTER.toJavaObject(jsonString, clazz);
    }

    public static <T> List<T> toList(String jsonStr, Class<T> clazz) {
        return JSON_ADAPTER.toList(jsonStr, clazz);
    }

    public static <V> Map<String, V> toMap(String jsonStr, Class<V> vClass) {
        return JSON_ADAPTER.toMap(jsonStr, vClass);
    }


}
