package com.ticho.trace.spring.config;

import com.ticho.trace.common.prop.TraceProperty;
import com.ticho.trace.spring.interceptor.TraceInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 链路bean初始化配置
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
@Configuration
@ConditionalOnClass(WebMvcConfigurer.class)
@ConditionalOnProperty(value = "ticho.trace.enable", havingValue = "true", matchIfMissing = true)
public class TraceBeanConfig {

    @Bean
    @ConfigurationProperties(prefix = "ticho.trace")
    public TraceProperty traceLogProperty() {
        return new TraceProperty();
    }

    @Bean
    public TraceInterceptor traceInterceptor(TraceProperty traceProperty, Environment environment) {
        return new TraceInterceptor(traceProperty, environment);
    }

}
