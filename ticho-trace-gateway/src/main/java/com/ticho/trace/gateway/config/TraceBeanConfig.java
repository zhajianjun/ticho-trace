package top.ticho.trace.gateway.config;

import top.ticho.trace.common.prop.TraceProperty;
import top.ticho.trace.gateway.filter.TraceGlobalFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * 链路bean初始化配置
 *
 * @author zhajianjun
 * @date 2023-04-03 11:49
 */
@Configuration
@ConditionalOnProperty(value = "ticho.trace.enable", havingValue = "true", matchIfMissing = true)
public class TraceBeanConfig {

    @Bean
    @ConfigurationProperties(prefix = "ticho.trace")
    public TraceProperty traceLogProperty() {
        return new TraceProperty();
    }

    @Bean
    public TraceGlobalFilter traceGlobalFilter(TraceProperty traceProperty, Environment environment) {
        return new TraceGlobalFilter(traceProperty, environment);
    }

}
