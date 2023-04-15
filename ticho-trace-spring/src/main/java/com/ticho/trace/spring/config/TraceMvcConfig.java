package com.ticho.trace.spring.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.ticho.trace.spring.interceptor.TraceInterceptor;

import javax.annotation.Resource;

/**
 * 链路MVC配置
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
@Configuration
@ConditionalOnClass(WebMvcConfigurer.class)
@ConditionalOnProperty(value = "ticho.trace.enable", havingValue = "true", matchIfMissing = true)
public class TraceMvcConfig implements WebMvcConfigurer {

    @Resource
    private TraceInterceptor traceInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(traceInterceptor).order(traceInterceptor.getOrder());
    }

}
