package com.ticho.trace.core.util;

import cn.hutool.json.JSONUtil;

import java.util.HashMap;
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

    public static String toJsonString(Object obj) {
        return JSONUtil.toJsonStr(obj);
    }

    public static <T> T toJavaObject(String jsonString, Class<T> clazz) {
        return JSONUtil.toBean(jsonString, clazz);
    }

    public static <T> List<T> toList(String jsonStr, Class<T> clazz) {
        return JSONUtil.toList(jsonStr, clazz);
    }

    public static <V> Map<String, V> toMap(String jsonStr, Class<V> vClass) {
        Map<String, Object> entries = JSONUtil.parseObj(jsonStr);
        Map<String, V> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : entries.entrySet()) {
            String key = entry.getKey();
            V value = vClass.cast(entry.getValue());
            result.put(key, value);
        }
        return result;
    }


}
