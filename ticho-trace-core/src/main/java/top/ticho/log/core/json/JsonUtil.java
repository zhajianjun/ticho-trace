package com.ticho.log.core.json;

import com.ticho.log.core.json.adapter.HutoolJsonAdapter;

import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-03-26 23:44:17
 */
public class JsonUtil {

    private JsonUtil() {
    }

    private static com.ticho.log.core.json.JsonAdapter JSON_ADAPTER;

    static {
        JSON_ADAPTER = new HutoolJsonAdapter();
    }

    public static void setJsonAdapter(com.ticho.log.core.json.JsonAdapter jsonAdapter) {
        JSON_ADAPTER = jsonAdapter;
    }


    public String toJsonString(Object obj) {
        return JSON_ADAPTER.toJsonString(obj);
    }

    public <T> T toJavaObject(String jsonString, Class<T> clazz) {
        return JSON_ADAPTER.toJavaObject(jsonString, clazz);
    }

    public <T> List<T> toList(String jsonStr, Class<T> clazz) {
        return JSON_ADAPTER.toList(jsonStr, clazz);
    }

    public <V> Map<String, V> toMap(String jsonStr, Class<V> vClass) {
        return JSON_ADAPTER.toMap(jsonStr, vClass);
    }


}
