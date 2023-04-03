package top.ticho.trace.gateway.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.ticho.trace.common.prop.TraceLogProperty;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-04-03 11:49
 */
@Configuration
@ConditionalOnProperty(value = "ticho.trace.enable", havingValue = "true", matchIfMissing = true)
public class TraceConfig {

    @Bean
    @ConfigurationProperties(prefix = "ticho.trace")
    public TraceLogProperty traceLogProperty(){
        return new TraceLogProperty();
    }

}
