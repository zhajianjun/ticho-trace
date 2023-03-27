package top.ticho.trace.core.push;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
public interface TracePushAdapter {

    void push(String url, Object data);

}
