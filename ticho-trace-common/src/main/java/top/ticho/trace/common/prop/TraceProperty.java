package top.ticho.trace.common.prop;

import lombok.Data;
import top.ticho.trace.common.constant.LogConst;

/**
 * 链路配置
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
@Data
public class TraceProperty {

    /** 是否开启链路拦截器 */
    private Boolean enable = true;
    /** 链路服务url */
    private String url;
    /** 日志收集服务url */
    private String logUrl;
    /** 链路服务秘钥 */
    private String secret;
    /** 链路表达式 */
    private String trace = LogConst.DEFAULT_TRACE;
    /** 是否推送日志 */
    private Boolean pushLog = false;
    /** 是否推送链路信息 */
    private Boolean pushTrace = false;
    /** 拦截器排序 */
    private Integer order = Integer.MIN_VALUE;

}
