package top.ticho.trace.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 日志配置
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
@Component
@Data
@ConfigurationProperties(prefix = "ticho.trace")
public class SpringTraceLogProperty {

    /** 是否开启链路拦截器 */
    private Boolean enable = true;

    /** 是否打印日志 */
    private Boolean print = false;

    /** 链路服务url */
    private String url;

    /** 链路服务秘钥 */
    private String secret;

    /** 日志打印前缀 */
    private String requestPrefixText = "[REQUEST]";

}
