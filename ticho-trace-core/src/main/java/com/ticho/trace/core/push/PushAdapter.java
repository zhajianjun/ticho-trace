package com.ticho.trace.core.push;

/**
 * 推送适配器
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
public interface PushAdapter {

    /**
     * 推送
     *
     * @param url url
     * @param secret 秘钥
     * @param data 数据
     */
    void push(String url, String secret, Object data);

}
