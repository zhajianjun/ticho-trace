package top.ticho.trace.core.json;

import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
public interface JsonAdapter {

    String toJsonString(Object obj);

    <T> T toJavaObject(String jsonString, Class<T> clazz);

    <T> List<T> toList(String jsonStr, Class<T> clazz);

    <V> Map<String, V> toMap(String jsonStr, Class<V> vClass);
}
