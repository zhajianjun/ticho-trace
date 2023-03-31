package top.ticho.trace.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.ticho.trace.spring.component.SpringTracePushContext;
import top.ticho.trace.spring.filter.WapperRequestFilter;
import top.ticho.trace.spring.interceptor.WebLogInterceptor;
import top.ticho.trace.spring.prop.SpringTraceLogProperty;

import javax.annotation.Resource;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
@Configuration
@ConditionalOnClass(WebMvcConfigurer.class)
@ConditionalOnProperty(value = "ticho.trace.enable", havingValue = "true", matchIfMissing = true)
public class SpringTraceConfig implements WebMvcConfigurer {

    @Resource
    private SpringTraceLogProperty springTraceLogProperty;

    @Resource
    private SpringTracePushContext springTracePushContext;

    @Resource
    private Environment environment;


    @Bean
    public WapperRequestFilter wapperRequestFilter() {
        return new WapperRequestFilter();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new WebLogInterceptor(springTraceLogProperty, springTracePushContext, environment)).order(Ordered.HIGHEST_PRECEDENCE + 10);
    }

}
