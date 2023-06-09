package com.ticho.trace.core.json.adapter;

import cn.hutool.json.JSONUtil;
import com.ticho.trace.core.json.JsonAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * hutool json工具适配类
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
public class HutoolJsonAdapter implements JsonAdapter {
    @Override
    public String toJsonString(Object obj) {
        return JSONUtil.toJsonStr(obj);
    }

    @Override
    public <T> T toJavaObject(String jsonString, Class<T> clazz) {
        return JSONUtil.toBean(jsonString, clazz);
    }

    @Override
    public <T> List<T> toList(String jsonStr, Class<T> clazz) {
        return JSONUtil.toList(jsonStr, clazz);
    }

    @Override
    public <V> Map<String, V> toMap(String jsonStr, Class<V> vClass) {
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
