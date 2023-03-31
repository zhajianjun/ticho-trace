package top.ticho.trace.core.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 日志
 *
 * @author zhajianjun
 * @date 2023-03-30 20:20:20
 */
@NoArgsConstructor
@Data
public class TichoLog {

    /**
     * 应用名称
     */
    private String appName;
    /**
     * 日志级别
     */
    private String logLevel;
    /**
     * 日志时间
     */
    private String dateTime;
    /**
     * 日志时间戳
     */
    private Long dtTime;
    /**
     * 类名称
     */
    private String className;
    /**
     * 方法名
     */
    private String method;
    /**
     * 序列号
     */
    private Long seq;
    /**
     * ip
     */
    private String ip;
    /**
     * 内容
     */
    private String content;
    /**
     * 线程名称
     */
    private String threadName;

}
